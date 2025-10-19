package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.CandidateEducationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateEducationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.CandidateModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UniversityModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateEducationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.CandidateRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.DegreeRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UniversityRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.CandidateEducationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateEducationService implements CandidateEducationServiceInterface {

    private final CandidateEducationRepository candidateEducationRepository;
    private final DegreeRepository degreeRepository;
    private final CandidateRepository candidateRepository;
    private final UniversityRepository universityRepository;

    private final ModelMapper modelMapper;

    @Override
    public CandidateEducationDto addCandidateEducation(CandidateEducationDto candidateEducationDto) {
        DegreeModel degree = degreeRepository.findById(candidateEducationDto.getDegree().getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree"," degreeId", candidateEducationDto.getDegree().getDegreeId().toString()));

        CandidateModel candidate = candidateRepository.findById(candidateEducationDto.getCandidate().getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate"," candidateId", candidateEducationDto.getCandidate().getCandidateId().toString()));

        UniversityModel university = universityRepository.findById(candidateEducationDto.getUniversity().getUniversityId())
                .orElseThrow(() -> new ResourceNotFoundException("University"," universityId", candidateEducationDto.getUniversity().getUniversityId().toString()));

        CandidateEducationModel candidateEducationModel = new CandidateEducationModel();
        candidateEducationModel.setDegree(degree);
        candidateEducationModel.setCandidate(candidate);
        candidateEducationModel.setUniversity(university);

        CandidateEducationModel newCandidateEducation = candidateEducationRepository.save(candidateEducationModel);
        return convertor(newCandidateEducation);
    }


    @Override
    public List<CandidateEducationDto> getAllCandidateEducations() {
        List<CandidateEducationModel> candidatesEducations = candidateEducationRepository.findAll();
//        if ()
        return List.of();
    }

    @Override
    public Optional<CandidateEducationDto> getCandidateEducationById(Integer candidateEducationId) {
        return Optional.empty();
    }

    @Override
    public CandidateEducationDto updateCandidateEducation(Integer candidateEducationId, CandidateEducationDto candidateEducationDto) {
        return null;
    }

    @Override
    public void deleteCandidateEducation(Integer candidateEducationId) {

    }

    @Override
    public List<CandidateEducationDto> getCandidateEducationByCandidateId(Integer candidateId) {
        return List.of();
    }

    @Override
    public List<CandidateEducationDto> getCandidateEducationByDegreeId(Integer degreeId) {
        return List.of();
    }

    @Override
    public List<CandidateEducationDto> getCandidateEducationByUniversityId(Integer universityId) {
        return List.of();
    }

    private CandidateEducationDto convertor(CandidateEducationModel entity) {
        return modelMapper.map(entity, CandidateEducationDto.class);
    }
    private CandidateEducationModel convertor(CandidateEducationDto entity) {
        return modelMapper.map(entity, CandidateEducationModel.class);
    }
}
