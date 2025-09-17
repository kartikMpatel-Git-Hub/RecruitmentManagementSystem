package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String resourceField;
    private String value;

    public ResourceNotFoundException(String resourceName,String resourceField,String value){
        super(String.format("%s Not Found With %s : %s",resourceName,resourceField,value));
    }
}
