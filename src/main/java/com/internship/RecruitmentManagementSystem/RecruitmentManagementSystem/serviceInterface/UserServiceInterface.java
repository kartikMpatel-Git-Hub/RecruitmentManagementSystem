package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;

import java.util.List;

public interface UserServiceInterface {

    public UserDto registerUser(UserDto userDto);
    public UserDto getUser(Integer userId);
    public List<UserDto> getUsers();

}
