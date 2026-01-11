package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register.RegisterUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.NewUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserChangePasswordDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface UserServiceInterface {

    Object registerUser(RegisterUserDto newUser, MultipartFile userImage);
    UserResponseDto getUser(Integer userId);
    PaginatedResponse<UserResponseDto> getUsers(Integer page,Integer size,String sortBy,String sortDir);
    PaginatedResponse<UserResponseDto> getCandidates(Integer page,Integer size,String sortBy,String sortDir);
    PaginatedResponse<UserResponseDto> getNonCandidates(Integer page,Integer size,String sortBy,String sortDir);
    PaginatedResponse<UserResponseDto> getInterviewers(Integer page,Integer size,String sortBy,String sortDir);
    PaginatedResponse<UserResponseDto> getHrs(Integer page,Integer size,String sortBy,String sortDir);
    UserResponseDto getUserByUserName(String userName);
    void deleteUser(Integer userId);
    UserResponseDto updateUser(UserUpdateDto userDto, Integer userId, AccountDetails accountDetails,MultipartFile userImage);
    UserResponseDto changePassword(UserModel currentUser, UserChangePasswordDto changePassword);

    UserResponseDto createUser(NewUserDto request);
}
