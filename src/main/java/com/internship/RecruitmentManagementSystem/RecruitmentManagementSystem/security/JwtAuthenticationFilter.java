package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtTokenHelper jwtTokenHelper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenHelper = jwtTokenHelper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Get Token
        String requestToken = request.getHeader("Authorization");

        String userName = null;

        String token = null;

        if(requestToken != null && requestToken.startsWith("Bearer ")){
            token = requestToken.substring(7);
            try {
                userName = jwtTokenHelper.getUserNameFromToken(token);
            }catch (IllegalArgumentException e){
                throw new JwtAuthenticationException("Unable To Get Jwt Token !");
            }catch (ExpiredJwtException e){
                throw new JwtAuthenticationException("Token Expire !");
            }catch (MalformedJwtException e){
                throw new JwtAuthenticationException("Invalid Jwt Token !");
            }
        }

        // validate Token

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if(jwtTokenHelper.validateToken(token,userDetails)){
                // Authentication
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }else{
                throw new JwtAuthenticationException("Invalid Jwt Token !");
            }
        }

        filterChain.doFilter(request,response);
    }
}
