package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceAlreadyExistsException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UniversityDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UniversityRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.UniversityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UniversityService implements UniversityServiceInterface {

    private final UniversityRepository universityRepository;
    private final ModelMapper modelMapper;

    @Override
    public UniversityDto addUniversity(UniversityDto university) {
        if(universityRepository.existsByUniversity(university.getUniversity())){
            throw new ResourceAlreadyExistsException("University already exists");
        }
        UniversityModel newUniversity = universityRepository.save(convertor(university));
        return convertor(newUniversity);
    }

    @Override
    public UniversityDto getUniversityByName(String universityName) {
        UniversityModel universityModel = universityRepository.findByUniversityName(universityName)
                .orElseThrow(()-> new ResourceNotFoundException("University","universityName",universityName));
        return convertor(universityModel);
    }

    @Override
    public UniversityDto getUniversityById(Integer universityId) {
        UniversityModel universityModel = universityRepository.findById(universityId)
                .orElseThrow(()-> new ResourceNotFoundException("University","universityId",universityId.toString()));
        return convertor(universityModel);
    }

    @Override
    public List<UniversityDto> getAllUniversities() {
        List<UniversityModel> universities = universityRepository.findAll();
        if(!universities.isEmpty()){
            return universities.stream().map(this::convertor).toList();
        }
        return null;
    }

    @Override
    public UniversityDto updateUniversity(Integer universityId, UniversityDto universityDto) {
        UniversityModel existingUniversity = universityRepository.findById(universityId)
                .orElseThrow(()-> new ResourceNotFoundException("University","universityId",universityId.toString()));
        if(universityDto.getUniversity() != null) existingUniversity.setUniversity(universityDto.getUniversity());
        UniversityModel updatedUniversity = universityRepository.save(existingUniversity);
        return convertor(updatedUniversity);
    }

    @Override
    public void deleteUniversity(Integer universityId) {
        UniversityModel existingUniversity = universityRepository.findById(universityId)
                .orElseThrow(()-> new ResourceNotFoundException("University","universityId",universityId.toString()));
        universityRepository.delete(existingUniversity);
    }

    private UniversityDto convertor(UniversityModel universityModel){
        return modelMapper.map(universityModel, UniversityDto.class);
    }

    private UniversityModel convertor(UniversityDto universityDto){
        return modelMapper.map(universityDto, UniversityModel.class);
    }
}
