package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;

import java.util.List;

public interface SkillServiceInterface {

    public SkillDto addSkill(SkillDto skillDto);
    public SkillDto getSkill(Integer skillId);
    public List<SkillDto> getSkills();
    public SkillDto updateSkill(SkillDto newSkill,Integer skillId);
    public void deleteSkill(Integer skillId);
    public SkillModel getBySkill(String skill);

}
