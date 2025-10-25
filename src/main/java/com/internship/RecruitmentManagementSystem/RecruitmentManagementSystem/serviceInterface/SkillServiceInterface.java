package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;

import java.util.List;

public interface SkillServiceInterface {

    public SkillDto addSkill(SkillDto skillDto);
    public SkillModel addSkillModel(SkillDto skillDto);
    public SkillDto getSkill(Integer skillId);
    SkillModel getSkillById(Integer skillId);
    public PaginatedResponse<SkillDto> getSkills(Integer page,Integer size,String sortBy,String sortDir);
    public SkillDto updateSkill(SkillDto newSkill,Integer skillId);
    public void deleteSkill(Integer skillId);
    public SkillModel getBySkill(String skill);

}
