package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.handler;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.JwtAuthenticationException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.CredentialException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        String message = ex.getMessage();

        ApiResponse apiResponse = new ApiResponse(404,message,false);

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> BadCredentialsExceptionHandler(BadCredentialsException ex){
        return new ResponseEntity<>(new ApiResponse(401,"Invalid Credentials",false), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiResponse> JwtAuthenticationExceptionHandler(JwtAuthenticationException ex) {
        return new ResponseEntity<>(
                new ApiResponse(401,ex.getMessage(), false),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<ApiResponse> CredentialExceptionHandler(CredentialException ex) {
        return new ResponseEntity<>(
                new ApiResponse(401,ex.getMessage(), false),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse> DisabledExceptionHandler(DisabledException ex) {
        return new ResponseEntity<>(
                new ApiResponse(401,"User Is Disabled !", false),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        Map<String,List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().stream().forEach(error->{
            String field = ((FieldError)error).getField();
            String message = error.getDefaultMessage();

            errors.computeIfAbsent(field,key->new ArrayList<>()).add(message);
        });


        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }
    //    ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,List<String>>> constraintViolationExceptionHandler(ConstraintViolationException ex){
        Map<String, List<String>> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error->{
            String field = error.getPropertyPath().toString();
            String message = error.getMessage();
            errors.computeIfAbsent(field,key->new ArrayList<>()).add(message);
        });
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    // HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> HttpRequestMethodNotSupportedHandler(HttpRequestMethodNotSupportedException ex){
        return new ResponseEntity<>(new ApiResponse(405,"Requested Method With This Path Not Found",false), HttpStatus.METHOD_NOT_ALLOWED);
    }
}
