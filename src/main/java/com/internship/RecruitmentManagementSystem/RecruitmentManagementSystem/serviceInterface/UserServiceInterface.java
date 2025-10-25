package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface UserServiceInterface {

    public UserDto registerUser(UserDto userDto,String roleName);
    public UserDto getUser(Integer userId);
    public PaginatedResponse<UserDto> getUsers(Integer page,Integer size,String sortBy,String sortDir);
    public PaginatedResponse<UserDto> getCandidates(Integer page,Integer size,String sortBy,String sortDir);
    public PaginatedResponse<UserDto> getNonCandidates(Integer page,Integer size,String sortBy,String sortDir);
    public UserDto getUserByUserName(String userName);
    public void deleteUser(Integer userId);
    public UserDto updateUser(UserDto userDto, Integer userId, AccountDetails accountDetails);
    public UserDto changePassword(UserModel currentUser, String oldPassword, String newPassword);
}
