package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.AppConstant;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.AccountDetails;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register.RegisterUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.NewUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserChangePasswordDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RegisterModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoleModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UserServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USERNAME_EXISTS = "Username already exists";
    private static final String EMAIL_EXISTS = "Email already exists";
    private static final String USERNAME_REQUIRED = "User Name Not Found!";
    private static final String PASSWORD_REQUIRED = "User Password Not Found!";
    private static final String EMAIL_REQUIRED = "User Email Not Found!";
    private static final String ROLE_REQUIRED = "User Role Not Found!";
    private static final String IMAGE_REQUIRED = "User Image Not Found!";
    private static final SecureRandom random = new SecureRandom();


    private final PasswordEncoder passwordEncoder;
    private final CandidateService candidateService;
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final EmailService emailService;
    private final ModelServiceInterface modelService;
    private final FileService fileService;
    private final Mapper mapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true),
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public Object registerUser(RegisterUserDto userDto,MultipartFile userImage) {
        logger.info("Attempting to register new user with username: {}", userDto.getUserName());
        List<String> errors = validateRegistrationRequest(userDto, userImage);
        if (!errors.isEmpty()) {
            logger.info("error Length : {}",errors.size());
            logger.warn("Registration validation failed for user: {} | Errors: {}", userDto.getUserName(), errors);
            throw new RegisterException(errors);
        }
        validateNewUser(userDto);
        String url = imageUpload(userImage);
        userDto.setUserImageUrl(url);
        logger.debug("Validation successful for new user: {}", userDto.getUserName());
        return register(userDto);
    }

    private String imageUpload(MultipartFile userImage) {
        logger.debug("Uploading user image");
        String fileName = null;
        try {
            fileName = fileService.uploadImage(userImage);
        } catch (InvalidImageFormateException e) {
            throw new FailedProcessException(e.getMessage());
        }
        logger.debug("Image uploaded successfully: {}", fileName);
        return fileName;
    }

    @Transactional
    private Object register(RegisterUserDto userDto) {
        logger.debug("Starting transactional registration for user: {} with role: {}", userDto.getUserName(), userDto.getRoleName());
        Integer roleId = AppConstant.getRoleId(userDto.getRoleName());
        RoleModel role = modelService.getRole(roleId);
        if (role.getRole().equalsIgnoreCase("candidate")) {
            UserModel userModel = mapper.toEntity(userDto, UserModel.class);
            userModel.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
            userModel.setRole(role);
            UserModel savedUser = modelService.addUser(userModel);
            logger.debug("Registering candidate profile for user ID: {}", savedUser.getUserId());
            candidateService.register(savedUser);
            return mapper.toDto(userModel,UserResponseDto.class);
        }else{
            RegisterModel user = mapper.toEntity(userDto, RegisterModel.class);
            user.setUserPassword(passwordEncoder.encode(userDto.getUserPassword()));
            user.setRole(role);
            RegisterModel savedUser = modelService.addRegister(user);
            logger.info("User registered Request Add successfully with ID: {} and Role: {}", savedUser.getRegisterId(),role.getRole());
            return mapper.toDto(savedUser, RegisterUserResponseDto.class);
        }
    }

    @Override
    @Cacheable(value = "currentUserData", key = "'id_' + #userId")
    public UserResponseDto getUser(Integer userId) {
        logger.info("Fetching user details for ID: {}", userId);
        UserModel user = modelService.getUser(userId);
        return mapper.toDto(user,UserResponseDto.class);
    }

    @Override
    @Cacheable(value = "userData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserResponseDto> getUsers(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getUsers(pageable),
                UserResponseDto.class
        );
    }

    @Override
    @Cacheable(value = "userCandidateData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserResponseDto> getCandidates(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching candidate users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getCandidates(pageable),
                UserResponseDto.class
        );
    }

    @Override
    @Cacheable(value = "userNonCandidateData", key = "'page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserResponseDto> getNonCandidates(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching non-candidate users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getNonCandidates(pageable),
                UserResponseDto.class
        );
    }

    @Override
    @Cacheable(value = "userData", key = "'interviewers_page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserResponseDto> getInterviewers(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching Interviewer users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getInterviewers(pageable),
                UserResponseDto.class
        );
    }

    @Override
    @Cacheable(value = "userData", key = "'hrs_page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<UserResponseDto> getHrs(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching hrs users page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getHrs(pageable),
                UserResponseDto.class
        );
    }

    @Override
    @Cacheable(value = "currentUserData", key = "'username_' + #userName")
    public UserResponseDto getUserByUserName(String userName) {
        logger.info("Fetching user by username: {}", userName);
        UserModel userModel = modelService.getUser(userName);
        return mapper.toDto(userModel,UserResponseDto.class);
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
        UserModel user = modelService.getUser(userId);
        if(user.getRole().getRole().equalsIgnoreCase("candidate")){
            CandidateModel candidate = modelService.getCandidateByUserId(userId);
            modelService.removeCandidate(candidate);
        }
        modelService.removeUser(user);
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
    public UserResponseDto updateUser(UserUpdateDto userDto, Integer userId, AccountDetails accountDetails,MultipartFile userImage) {
        logger.info("Updating user ID: {}", userId);
        if(userImage!=null && !userImage.isEmpty()) {
            String imageUrl = imageUpload(userImage);
            logger.info(imageUrl);
            userDto.setUserImageUrl(imageUrl);
        }
        logger.info(userDto.getUserImageUrl());
        UserModel user = modelService.getUser(userId);
        updateUserFields(user, userDto);
        UserModel updatedUser = modelService.addUser(user);
        logger.info("User ID: {} updated successfully", userId);
        return mapper.toDto(updatedUser,UserResponseDto.class);
    }

    @Transactional
    @Override
    public UserResponseDto changePassword(UserModel currentUser, UserChangePasswordDto changePassword) {
        logger.info("Changing password for user ID: {}", currentUser.getUserId());

        if (changePassword.getCurrentPassword().equalsIgnoreCase(changePassword.getNewPassword())) {
            logger.error("New password must be different from old password for user ID: {}", currentUser.getUserId());
            throw new IllegalArgumentException("New Password And Old Password Must Be Different !");
        }

        passwordValidation(changePassword.getNewPassword());

        if (!passwordEncoder.matches(changePassword.getCurrentPassword(), currentUser.getUserPassword())) {
            logger.error("Incorrect old password for user ID: {}", currentUser.getUserId());
            throw new IllegalArgumentException("Current password is incorrect");
        }

        currentUser.setUserPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        modelService.addUser(currentUser);
        logger.info("Password updated successfully for user ID: {}", currentUser.getUserId());
        return mapper.toDto(currentUser,UserResponseDto.class);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true),
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public UserResponseDto createUser(NewUserDto request) {
        validateNewUser(request);
        UserModel entity = new UserModel();
        entity.setUserName(request.getUserName());
        entity.setUserEmail(request.getUserEmail());
        String password = generatePassword();
        entity.setUserPassword(passwordEncoder.encode(password));
        entity.setRole(modelService.getRole(request.getUserRole()));
        UserModel userModel = modelService.addUser(entity);
        emailService.mailToCredentialCandidate(userModel.getUserEmail(),userModel.getUsername(),password);
        return mapper.toDto(userModel,UserResponseDto.class);
    }

    private void validateNewUser(NewUserDto request) {
        if(modelService.existsUserByEmail(request.getUserEmail()))
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS);
        if(modelService.existedUserByUserName(request.getUserName()))
            throw new ResourceAlreadyExistsException(USERNAME_EXISTS);
        if(request.getUserName()==null || request.getUserName().trim().isEmpty())
            throw new SomethingWrongException(USERNAME_REQUIRED);
    }

    private void validateNewUser(RegisterUserDto userDto) {
        logger.debug("Validating new user for duplicates: username={}, email={}", userDto.getUserName(), userDto.getUserEmail());

        if (modelService.existedUserByUserName(userDto.getUserName())) {
            logger.error("Username already exists: {}", userDto.getUserName());
            throw new ResourceAlreadyExistsException(USERNAME_EXISTS);
        }
        if (modelService.existsUserByEmail(userDto.getUserEmail())) {
            logger.error("Email already exists: {}", userDto.getUserEmail());
            throw new ResourceAlreadyExistsException(EMAIL_EXISTS);
        }
    }

    private void updateUserFields(UserModel user, UserUpdateDto userDto) {
        logger.debug("Updating fields for user ID: {}", user.getUserId());

        if (userDto.getUserName() != null && !userDto.getUserName().trim().isEmpty()) {
            user.setUserName(userDto.getUserName());
        }

        if (userDto.getUserEmail() != null && !userDto.getUserEmail().trim().isEmpty()) {
            validateEmailUpdate(user, userDto.getUserEmail());
            user.setUserEmail(userDto.getUserEmail());
        }

        if (userDto.getUserImageUrl() != null && !userDto.getUserImageUrl().trim().isEmpty()) {
            logger.info("Updating user image URL for user ID: {}", user.getUserId());
            user.setUserImageUrl(userDto.getUserImageUrl());
        }

        if (userDto.getUserEnabled() != null)
            user.setUserEnabled(userDto.getUserEnabled());
    }

    private void validateEmailUpdate(UserModel user, String newEmail) {
        if (!newEmail.equals(user.getUserEmail()) && modelService.existsUserByEmail(newEmail)) {
            logger.error("Email already exists while updating user ID: {}", user.getUserId());
            throw new CustomFieldAlreadyExistsException("userEmail", EMAIL_EXISTS);
        }
    }

    private void passwordValidation(String password) {
        if (password.length() < 8) {
            logger.error("Password validation failed: less than 8 characters");
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        logger.debug("Password validation passed");
    }

    private List<String> validateRegistrationRequest(RegisterUserDto request, MultipartFile userImage) {
        List<String> errors = new ArrayList<>();
        if (userImage == null || userImage.isEmpty()) errors.add(IMAGE_REQUIRED);
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getUserEmail())) errors.add(EMAIL_REQUIRED);
        if (isNullOrEmpty(request.getUserPassword())) errors.add(PASSWORD_REQUIRED);
        if (isNullOrEmpty(request.getRoleName())) errors.add(ROLE_REQUIRED);
        return errors;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String generatePassword() {
        logger.debug("Generating random password");

        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SPECIAL = "@#$%&*!";
        String ALL = UPPER + LOWER + DIGITS + SPECIAL;

        StringBuilder password = new StringBuilder(8);

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < 8; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        char[] pwdArray = password.toString().toCharArray();
        for (int i = 0; i < pwdArray.length; i++) {
            int j = random.nextInt(pwdArray.length);
            char temp = pwdArray[i];
            pwdArray[i] = pwdArray[j];
            pwdArray[j] = temp;
        }

        String finalPassword = new String(pwdArray);

        logger.debug("Generated password (masked): ********");
        return finalPassword;
    }

}
