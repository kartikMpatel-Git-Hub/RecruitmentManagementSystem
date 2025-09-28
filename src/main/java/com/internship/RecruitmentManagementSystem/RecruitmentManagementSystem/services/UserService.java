package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.CustomFieldAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UserServiceInterface;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USER_NOT_FOUND = "User not found with %s: %s";
    private static final String ROLE_NOT_FOUND = "Role not found with %s: %s";
    private static final String USERNAME_EXISTS = "Username already exists";
    private static final String EMAIL_EXISTS = "Email already exists";

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
    @Transactional
    public UserDto registerUser(UserDto userDto, String roleName) {
        logger.info("Registering new user with username: {}", userDto.getUserName());

        validateNewUser(userDto);

        UserModel user = convertor(userDto);
        Integer roleId = AppConstant.getRoleId(roleName);

        user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        RoleModel role = findRoleById(roleId, roleName);
        user.getRoles().add(role);

        UserModel savedUser = userRepository.save(user);
        logger.info("Successfully registered user with ID: {}", savedUser.getUserId());

        return convertor(savedUser);
    }

    @Override
    public UserDto getUser(Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        return convertor(findUserById(userId));
    }

    @Override
    public List<UserDto> getUsers() {
        logger.info("Fetching all users");
        List<UserModel> userModels = userRepository.findAll();
        return userModels.stream().map(this::convertor).toList();
    }

    @Override
    public UserDto getUserByUserName(String userName) {
        logger.info("Fetching user by username: {}", userName);
        UserModel userModel = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "userName", userName));
        return convertor(userModel);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        logger.info("Deleting user with ID: {}", userId);
        UserModel user = findUserById(userId);
        user.getRoles().clear();
        userRepository.delete(user);
        logger.info("Successfully deleted user with ID: {}", userId);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Integer userId, AccountDetails accountDetails) {
        logger.info("Updating user with ID: {}", userId);
        UserModel user = findUserById(userId);

        updateUserFields(user, userDto);
        UserModel updatedUser = userRepository.save(user);
        logger.info("Successfully updated user with ID: {}", userId);

        return convertor(updatedUser);
    }

    private void validateNewUser(UserDto userDto) {
        if (userRepository.existsByUserName(userDto.getUserName())) {
            throw new CustomFieldAlreadyExistsException("userName", USERNAME_EXISTS);
        }
        if (userRepository.existsByUserEmail(userDto.getUserEmail())) {
            throw new CustomFieldAlreadyExistsException("userEmail", EMAIL_EXISTS);
        }
    }

    private UserModel findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "ID", userId.toString()));
    }

    private RoleModel findRoleById(Integer roleId, String roleName) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "role", roleName));
    }

    private void updateUserFields(UserModel user, UserDto userDto) {
        if (userDto.getUserEmail() != null && !userDto.getUserEmail().trim().isEmpty()) {
            validateEmailUpdate(user, userDto.getUserEmail());
            user.setUserEmail(userDto.getUserEmail());
        }

        if (userDto.getUserPassword() != null && !userDto.getUserPassword().trim().isEmpty()) {
            user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        }

        if (userDto.getUserImageUrl() != null && !userDto.getUserImageUrl().trim().isEmpty()) {
            user.setUserImageUrl(userDto.getUserImageUrl());
        }
    }

    private void validateEmailUpdate(UserModel user, String newEmail) {
        if (!newEmail.equals(user.getUserEmail()) && userRepository.existsByUserEmail(newEmail)) {
            throw new CustomFieldAlreadyExistsException("userEmail", EMAIL_EXISTS);
        }
    }

    private UserDto convertor(UserModel userModel) {
        return modelMapper.map(userModel, UserDto.class);
    }

    private UserModel convertor(UserDto userDto) {
        return modelMapper.map(userDto, UserModel.class);
    }

}
