package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenHelper {
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    private final UserService userService;

    public JwtTokenHelper(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void initializeKey(){
        this.key  = Keys.hmacShaKeyFor(secret.getBytes());
//        System.out.println("JWT ALGO KEY : " + key);
    }

    public String getUserNameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationTime(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims,T> claimResolver){
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder().
                setSigningKey(key).
                build().
                parseClaimsJws(token).
                getBody();
    }

    public  Boolean isTokenExpire(String token){
        final Date expiration = getExpirationTime(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        UserResponseDto user = userService.getUserByUserName(userDetails.getUsername());
        claims.put("userType",user.getRole().getRole());
        claims.put("userEmail",user.getUserEmail());
        claims.put("userName",userDetails.getUsername());
        return doGenerateToken(claims,userDetails.getUsername());
    }

    private String doGenerateToken(Map<String,Object> claims,String subject){

        return Jwts.
                builder().
                setClaims(claims).
                setSubject(subject).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis()+ JWT_TOKEN_VALIDITY * 1000)).
                signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token,UserDetails userDetails){
        final String userName = getUserNameFromToken(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpire(token);
    }
}
