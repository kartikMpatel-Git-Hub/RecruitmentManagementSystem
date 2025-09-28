package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;

import java.util.List;

public interface UserServiceInterface {

    public UserDto registerUser(UserDto userDto,String roleName);
    public UserDto getUser(Integer userId);
    public List<UserDto> getUsers();
    public UserDto getUserByUserName(String userName);
    public void deleteUser(Integer userId);
    public UserDto updateUser(UserDto userDto, Integer userId, AccountDetails accountDetails);
}
