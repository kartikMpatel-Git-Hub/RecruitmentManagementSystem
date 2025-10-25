package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.CustomFieldAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RoleRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UserServiceInterface;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CandidateService candidateService;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, CandidateService candidateService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.candidateService = candidateService;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true)
    })
    public UserDto registerUser(UserDto userDto, String roleName) {
        logger.info("Attempting to register new user with username: {}", userDto.getUserName());
        validateNewUser(userDto);
        logger.debug("Validation successful for new user: {}", userDto.getUserName());
        return register(userDto, roleName);
    }

    @Transactional
    private UserDto register(UserDto userDto, String roleName) {
        logger.debug("Starting transactional registration for user: {} with role: {}", userDto.getUserName(), roleName);
        UserModel user = convertor(userDto);
        Integer roleId = AppConstant.getRoleId(roleName);

        user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        RoleModel role = findRoleById(roleId, roleName);
        user.setRole(role);

        UserModel savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {} and Role: {}", savedUser.getUserId(), roleName);

        if (roleName.equals("CANDIDATE")) {
            logger.debug("Registering candidate profile for user ID: {}", savedUser.getUserId());
            candidateService.register(savedUser);
        }

        return convertor(savedUser);
    }

    @Override
    @Cacheable(value = "currentUserData", key = "'id_' + #userId")
    public UserDto getUser(Integer userId) {
        logger.info("Fetching user details for ID: {}", userId);
        return convertor(findUserById(userId));
    }

    @Override
    @Cacheable(value = "userData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserDto> getUsers(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedUsers(userRepository.findAll(getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "userCandidateData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserDto> getCandidates(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching candidate users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedUsers(userRepository.findCandidate(getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Cacheable(value = "userNonCandidateData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserDto> getNonCandidates(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching non-candidate users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedUsers(userRepository.findNonCandidate(getPageable(page, size, sortBy, sortDir)));
    }

    private PaginatedResponse<UserDto> getPaginatedUsers(Page<UserModel> pageResponse) {
        logger.debug("Building PaginatedResponse for {} users", pageResponse.getContent().size());
        PaginatedResponse<UserDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    @Override
    @Cacheable(value = "currentUserData", key = "'username_' + #userName")
    public UserDto getUserByUserName(String userName) {
        logger.info("Fetching user by username: {}", userName);
        UserModel userModel = userRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", userName);
                    return new ResourceNotFoundException("USER", "userName", userName);
                });
        return convertor(userModel);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "currentUserData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true)
    })
    public void deleteUser(Integer userId) {
        logger.info("Deleting user with ID: {}", userId);
        UserModel user = findUserById(userId);
        userRepository.delete(user);
        logger.info("Successfully deleted user ID: {}", userId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "currentUserData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true)
    })
    public UserDto updateUser(UserDto userDto, Integer userId, AccountDetails accountDetails) {
        logger.info("Updating user ID: {}", userId);
        UserModel user = findUserById(userId);
        updateUserFields(user, userDto);
        UserModel updatedUser = userRepository.save(user);
        logger.info("User ID: {} updated successfully", userId);
        return convertor(updatedUser);
    }

    @Transactional
    @Override
    public UserDto changePassword(UserModel currentUser, String oldPassword, String newPassword) {
        logger.info("Changing password for user ID: {}", currentUser.getUserId());

        if (oldPassword.equalsIgnoreCase(newPassword)) {
            logger.error("New password must be different from old password for user ID: {}", currentUser.getUserId());
            throw new IllegalArgumentException("New Password And Old Password Must Be Different !");
        }

        passwordValidation(newPassword);

        if (!passwordEncoder.matches(oldPassword, currentUser.getUserPassword())) {
            logger.error("Incorrect old password for user ID: {}", currentUser.getUserId());
            throw new IllegalArgumentException("Current password is incorrect");
        }

        currentUser.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        logger.info("Password updated successfully for user ID: {}", currentUser.getUserId());
        return convertor(currentUser);
    }

    private void validateNewUser(UserDto userDto) {
        logger.debug("Validating new user for duplicates: username={}, email={}", userDto.getUserName(), userDto.getUserEmail());

        if (userRepository.existsByUserName(userDto.getUserName())) {
            logger.error("Username already exists: {}", userDto.getUserName());
            throw new ResourceAlreadyExistsException(USERNAME_EXISTS);
        }
        if (userRepository.existsByUserEmail(userDto.getUserEmail())) {
            logger.error("Email already exists: {}", userDto.getUserEmail());
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS);
        }
    }

    private UserModel findUserById(Integer userId) {
        logger.debug("Fetching user from DB by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for ID: {}", userId);
                    return new ResourceNotFoundException("USER", "ID", userId.toString());
                });
    }

    private RoleModel findRoleById(Integer roleId, String roleName) {
        logger.debug("Fetching role from DB by ID: {}", roleId);
        return roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    logger.error("Role not found: {}", roleName);
                    return new ResourceNotFoundException("Role", "role", roleName);
                });
    }

    private void updateUserFields(UserModel user, UserDto userDto) {
        logger.debug("Updating fields for user ID: {}", user.getUserId());

        if (userDto.getUserName() != null && !userDto.getUserName().trim().isEmpty()) {
            user.setUserName(userDto.getUserName());
        }

        if (userDto.getUserEmail() != null && !userDto.getUserEmail().trim().isEmpty()) {
            validateEmailUpdate(user, userDto.getUserEmail());
            user.setUserEmail(userDto.getUserEmail());
        }

        if (userDto.getUserPassword() != null && !userDto.getUserPassword().trim().isEmpty()) {
            logger.debug("Encoding and updating password for user ID: {}", user.getUserId());
            user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
        }

        if (userDto.getUserImageUrl() != null && !userDto.getUserImageUrl().trim().isEmpty()) {
            user.setUserImageUrl(userDto.getUserImageUrl());
        }

        if (userDto.getUserEnabled() != null)
            user.setUserEnabled(userDto.getUserEnabled());
    }

    private void validateEmailUpdate(UserModel user, String newEmail) {
        if (!newEmail.equals(user.getUserEmail()) && userRepository.existsByUserEmail(newEmail)) {
            logger.error("Email already exists while updating user ID: {}", user.getUserId());
            throw new CustomFieldAlreadyExistsException("userEmail", EMAIL_EXISTS);
        }
    }

    private UserDto convertor(UserModel userModel) {
        logger.trace("Mapping UserModel -> UserDto for ID: {}", userModel.getUserId());
        return modelMapper.map(userModel, UserDto.class);
    }

    private UserModel convertor(UserDto userDto) {
        logger.trace("Mapping UserDto -> UserModel for username: {}", userDto.getUserName());
        return modelMapper.map(userDto, UserModel.class);
    }

    private void passwordValidation(String password) {
        if (password.length() < 8) {
            logger.error("Password validation failed: less than 8 characters");
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        logger.debug("Password validation passed");
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }
}
