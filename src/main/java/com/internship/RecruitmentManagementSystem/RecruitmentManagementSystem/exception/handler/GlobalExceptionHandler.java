package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.handler;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiObjectResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.security.auth.login.CredentialException;
import java.sql.SQLIntegrityConstraintViolationException;
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
    public ResponseEntity<?> DisabledExceptionHandler(DisabledException ex) {
        return new ResponseEntity<>(
                "User is disabled, please contact admin",
                HttpStatus.BAD_REQUEST
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

    // SQLIntegrityConstraintViolationException
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage(); // e.g., "Duplicate entry 'kartik' for key 'tbl_user.UK6jr81l5qqpxjp72fgi23ubqc9'"
        String userFriendlyMessage = "Duplicate value";

        if (message != null && message.contains("Duplicate entry")) {
            if (message.contains("user_name")) {
                userFriendlyMessage = "Username already exists";
            }
            // You can check for other fields too:
            // else if (message.contains("email")) { userFriendlyMessage = "Email already exists"; }
        }

        return new ResponseEntity<>(new ApiResponse(400, userFriendlyMessage, false), HttpStatus.BAD_REQUEST);
    }

    // CustomFieldAlreadyExistsException
    @ExceptionHandler(CustomFieldAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> CustomFieldAlreadyExistsExceptionHandler(CustomFieldAlreadyExistsException ex){
        String error = ex.getMessage();
        return new ResponseEntity<>(new ApiResponse(400, error, false), HttpStatus.BAD_REQUEST);
    }

    // ErrorResponseException
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> ErrorResponseExceptionHandler(ErrorResponseException ex){
        String error = ex.getMessage();
        return new ResponseEntity<>(ex.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> DuplicateEntryHandler(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                new ApiObjectResponse(400, "Duplicate entry: value already exists", false),
                HttpStatus.BAD_REQUEST
        );
    }
//    ResourceAlreadyExistsException
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> ResourceAlreadyExistsExceptionHandler(ResourceAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }

    // FailedProcessException
    @ExceptionHandler(FailedProcessException.class)
    public ResponseEntity<?> FailedProcessExceptionHandler(FailedProcessException ex) {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }
//    IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> IllegalArgumentExceptionHandler(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }
//  MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> MaxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>("Image Size Limit Reached !",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("Stream")) {
            return new ResponseEntity<>(
                    new ErrorResponse(400,"Invalid Value",List.of("Must be arts, commerce, science"),false),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                new ApiObjectResponse(
                        400,
                        "Malformed JSON request: " + ex.getMessage(),
                        false
                ),
                HttpStatus.BAD_REQUEST
        );
    }

//    UnexpectedRollbackException
//    @ExceptionHandler(UnexpectedRollbackException.class)
//    public ResponseEntity<?> UnexpectedRollbackExceptionHandler(UnexpectedRollbackException ex) {
//        return new ResponseEntity<>("Something Went Wrong !",HttpStatus.BAD_REQUEST);
//    }
}
