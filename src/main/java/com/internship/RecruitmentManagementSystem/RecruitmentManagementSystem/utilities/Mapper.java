package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Mapper {

    private static final Logger logger = LoggerFactory.getLogger(Mapper.class);

    private final ModelMapper mapper;

    public <D> D toDto(Object entity, Class<D> className){
        logger.debug("Mapping entity to DTO. TargetClass={}", className.getSimpleName());
        D dto = mapper.map(entity, className);
        logger.debug("Entity mapped successfully to DTO. TargetClass={}", className.getSimpleName());
        return dto;
    }

    public <E> E toEntity(Object dto, Class<E> className){
        logger.debug("Mapping DTO to Entity. TargetClass={}", className.getSimpleName());
        E entity = mapper.map(dto, className);
        logger.debug("DTO mapped successfully to Entity. TargetClass={}", className.getSimpleName());
        return entity;
    }
}

