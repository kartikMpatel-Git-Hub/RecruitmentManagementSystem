package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.mappers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillDto toDto(SkillModel entity);
    SkillModel toEntity(SkillDto dto);
}
