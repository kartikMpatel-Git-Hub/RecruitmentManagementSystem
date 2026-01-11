package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillUpdateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

public interface SkillServiceInterface {

    SkillResponseDto addSkill(SkillCreateDto skillDto);
    SkillResponseDto getSkill(Integer skillId);
    PaginatedResponse<SkillResponseDto> getSkills(Integer page,Integer size,String sortBy,String sortDir);
    SkillResponseDto updateSkill(SkillUpdateDto newSkill, Integer skillId);
    void deleteSkill(Integer skillId);
    SkillModel getBySkill(String skill);

}
