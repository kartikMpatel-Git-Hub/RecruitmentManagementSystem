package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.SkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface SkillServiceInterface {

    SkillResponseDto addSkill(SkillCreateDto skillDto);
    SkillResponseDto getSkill(Integer skillId);
    SkillModel getSkillById(Integer skillId);
    PaginatedResponse<SkillResponseDto> getSkills(Integer page,Integer size,String sortBy,String sortDir);
    SkillResponseDto updateSkill(SkillUpdateDto newSkill, Integer skillId);
    void deleteSkill(Integer skillId);
    SkillModel getBySkill(String skill);

}
