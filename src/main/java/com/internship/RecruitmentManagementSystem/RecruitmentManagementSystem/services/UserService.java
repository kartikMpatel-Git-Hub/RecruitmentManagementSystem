package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UserServiceInterface;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto registerUser(UserDto userDto) {

        UserModel user = convertor(userDto);
        user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        RoleModel role = roleRepository.findById(AppConstant.NORMAL_ROLE).orElseThrow(
                ()->new ResourceNotFoundException("Role","Id",AppConstant.NORMAL_ROLE.toString()));
        user.getRoles().add(role);
        UserModel savedUser = userRepository.save(user);
        return convertor(savedUser);
    }

    @Override
    public UserDto getUser(Integer userId) {
        UserModel user = userRepository
                .findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User","id",userId.toString()));

        return this.convertor(user);
    }

    @Override
    public List<UserDto> getUsers() {

        List<UserModel> userModels = userRepository.findAll();

        return userModels.stream().map(this::convertor).toList();
    }

    private UserDto convertor(UserModel userModel){
        return modelMapper.map(userModel,UserDto.class);
    }
    private UserModel convertor(UserDto userDto){
        return modelMapper.map(userDto,UserModel.class);
    }

}
