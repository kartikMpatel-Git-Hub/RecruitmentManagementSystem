package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ApiResponse apiResponse =
                new ApiResponse(401,"Unauthorized !",false);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
