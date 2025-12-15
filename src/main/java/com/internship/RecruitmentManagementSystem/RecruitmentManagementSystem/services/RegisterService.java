package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.role.RoleResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RegisterModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.RegisterRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.RegisterServiceInterface;
import lombok.Data;
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
import org.springframework.stereotype.Service;

@Data
@Service
public class RegisterService implements RegisterServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    private final RegisterRepository registerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Cacheable(value = "registerRequest", key = "'requests_page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<RegisterUserResponseDto> getAllRequest(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching register Request page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        return getPaginatedRegisterRequest(registerRepository.findAll(getPageable(page, size, sortBy, sortDir)));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true),
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public UserResponseDto acceptRequest(Integer registerId) {
        RegisterModel registerRequest = registerRepository.findById(registerId).orElseThrow(
                ()-> new ResourceNotFoundException("RegisterModel","registerId",registerId.toString())
        );
        UserModel userModel = convertorToUser(registerRequest);
        UserModel savedUser = userRepository.save(userModel);

        registerRepository.delete(registerRequest);

        return convertor(savedUser);
    }

    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userCandidateData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true),
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    private UserModel convertorToUser(RegisterModel model)  {
        UserModel res = new UserModel();

        res.setUserName(model.getUserName());
        res.setUserEmail(model.getUserEmail());
        res.setUserImageUrl(model.getUserImageUrl());
        res.setRole(model.getRole());
        res.setUserPassword(model.getUserPassword());

        return res;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public void rejectRequest(Integer registerId) {
        RegisterModel registerRequest = registerRepository.findById(registerId).orElseThrow(
                ()-> new ResourceNotFoundException("RegisterModel","registerId",registerId.toString())
        );
        registerRepository.delete(registerRequest);
    }

    @Override
    public long countRequest() {
        return registerRepository.count();
    }

    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PaginatedResponse<RegisterUserResponseDto> getPaginatedRegisterRequest(Page<RegisterModel> pageResponse) {
        logger.debug("Building PaginatedResponse for {} users", pageResponse.getContent().size());
        PaginatedResponse<RegisterUserResponseDto> response = new PaginatedResponse<>();
        response.setData(pageResponse.stream().map(this::convertor).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private RegisterUserResponseDto convertor(RegisterModel model) {
        RegisterUserResponseDto dto = new RegisterUserResponseDto();

        dto.setRegisterId(model.getRegisterId());
        dto.setRole(model.getRole().getRole());
        dto.setUserEmail(model.getUserEmail());
        dto.setUserName(model.getUserName());
        dto.setUserImageUrl(model.getUserImageUrl());

        return dto;
    }

    private UserResponseDto convertor(UserModel entity) {
        logger.trace("Mapping UserModel -> UserResponseDto for ID: {}", entity.getUserId());
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(entity.getUserId());
        userResponseDto.setUserName(entity.getUsername());
        userResponseDto.setUserEmail(entity.getUserEmail());
        userResponseDto.setUserImageUrl(entity.getUserImageUrl());
        userResponseDto.setRole(modelMapper.map(entity.getRole(), RoleResponseDto.class));
        userResponseDto.setCreatedAt(entity.getCreatedAt());
        userResponseDto.setUpdatedAt(entity.getUpdatedAt());
        userResponseDto.setUserEnabled(entity.getUserEnabled());
        return userResponseDto;
    }

}
