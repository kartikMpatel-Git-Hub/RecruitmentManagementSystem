package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.SkillModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.SkillRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.SkillServiceInterface;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService implements SkillServiceInterface {

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public SkillService(SkillRepository skillRepository, ModelMapper modelMapper) {
        this.skillRepository = skillRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SkillDto addSkill(SkillDto skillDto) {
        SkillModel skill = convertor(skillDto);
        SkillModel savedSkill = skillRepository.save(skill);
        return convertor(savedSkill);
    }

    @Override
    public SkillModel addSkillModel(SkillDto skillDto) {
        SkillModel skill = convertor(skillDto);
        return skillRepository.save(skill);
    }

    @Override
    public SkillDto getSkill(Integer skillId) {
        SkillModel skill = skillRepository.findById(skillId).orElseThrow(
                () -> new ResourceNotFoundException("Skill", "skillId", skillId.toString())
        );
        return convertor(skill);
    }

    @Override
    public SkillModel getSkillById(Integer skillId) {
        return skillRepository.findById(skillId).orElseThrow(
                () -> new ResourceNotFoundException("Skill", "skillId", skillId.toString())
        );
    }

    @Override
    public List<SkillDto> getSkills() {

        List<SkillModel> skills = skillRepository.findAll();

        return skills.stream().map(this::convertor).toList();
    }

    @Override
    public SkillDto updateSkill(SkillDto newSkill, Integer skillId) {

        SkillModel oldSkill = skillRepository.findById(skillId).orElseThrow(
                () -> new ResourceNotFoundException("Skill", "skillId", skillId.toString())
        );

        oldSkill.setSkill(newSkill.getSkill());
        SkillModel updatedSkill = skillRepository.save(oldSkill);

        return convertor(updatedSkill);
    }

    @Override
    public void deleteSkill(Integer skillId) {
        SkillModel oldSkill = skillRepository.findById(skillId).orElseThrow(
                () -> new ResourceNotFoundException("Skill", "skillId", skillId.toString())
        );

        skillRepository.delete(oldSkill);
    }

    @Override
    public SkillModel getBySkill(String skill) {
        return skillRepository.findBySkill(skill).orElse(null);
    }

    private SkillDto convertor(SkillModel skill) {
        return modelMapper.map(skill, SkillDto.class);
    }

    private SkillModel convertor(SkillDto skill) {
        return modelMapper.map(skill, SkillModel.class);
    }
}
