package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RegisterModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.RegisterServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.Mapper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.PaginatedResponseCreator;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Data
@Service
public class RegisterService implements RegisterServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
    private final PaginatedResponseCreator paginatedResponseCreator;
    private final ModelServiceInterface modelService;
    private final Mapper mapper;

    @Override
    @Cacheable(value = "registerRequest", key = "'requests_page_'+#page+'_'+'size_'+#size+'_'+'sortBy_'+#sortBy+'_'+'sortDir_'+#sortDir")
    public PaginatedResponse<RegisterUserResponseDto> getAllRequest(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Fetching register Request page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        Pageable pageable = paginatedResponseCreator.getPageable(page, size, sortBy, sortDir);
        return paginatedResponseCreator.getPaginatedResponse(
                modelService.getAllRequests(pageable),
                RegisterUserResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userData", allEntries = true),
            @CacheEvict(value = "userNonCandidateData", allEntries = true),
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public UserResponseDto acceptRequest(Integer registerId) {
        RegisterModel registerRequest = modelService.getRegister(registerId);
        UserModel userModel = mapper.toEntity(registerRequest, UserModel.class);
        UserModel savedUser = modelService.addUser(userModel);

        modelService.removeRegister(registerRequest);
        return mapper.toDto(savedUser, UserResponseDto.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "registerRequest", allEntries = true)
    })
    public void rejectRequest(Integer registerId) {
        RegisterModel registerRequest = modelService.getRegister(registerId);
        modelService.removeRegister(registerRequest);
    }

    @Override
    public long countRequest() {
        return modelService.countRegisterRequest();
    }
}
