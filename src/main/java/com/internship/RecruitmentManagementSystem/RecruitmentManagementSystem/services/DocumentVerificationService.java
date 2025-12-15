package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.SomethingWrongException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.documentVerification.DocumentReviewRequestDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification.DocumentResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.documentVerification.DocumentVerificationResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DocumentModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DocumentVerificationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.ApplicationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.DocumentRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.DocumentVerificationRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DocumentVerificationServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities.HtmlTemplateBuilder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
public class DocumentVerificationService implements DocumentVerificationServiceInterface {

    private static Logger logger = LoggerFactory.getLogger(DocumentVerificationService.class);

    private final DocumentVerificationRepository documentVerificationRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final HtmlTemplateBuilder templateBuilder;
    private final MatchingScoreService matchingScoreService;
    private final EmailService emailService;
    private final FileService fileService;
    @Value("${project.image}")
    private String path;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "documentVerification",allEntries = true),
            @CacheEvict(value = "applicationData",allEntries = true)
        }
    )
    public void uploadDocument(Integer applicationId,
                               MultipartFile file) throws InvalidImageFormateException, IOException {
        DocumentVerificationModel verification =
                documentVerificationRepository.findByApplicationApplicationId(applicationId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("DocumentVerificationModel","applicationId",applicationId.toString())
                        );


        if (verification.getVerificationStatus() == DocumentVerificationStatus.APPROVED) {
            throw new SomethingWrongException("Document verification already completed. Upload not allowed.");
        }

        if (verification.getVerificationStatus() == DocumentVerificationStatus.REJECTED) {
            verification.setVerificationStatus(DocumentVerificationStatus.PENDING);
        }

        DocumentModel document = new DocumentModel();
        document.setDocumentName(file.getOriginalFilename());
        document.setDocumentUrl(fileService.uploadImage(path,file));
        document.setDocumentStatus(DocumentStatus.UPLOADED);
        document.setDocumentVerification(verification);

        verification.getDocuments().add(document);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "documentVerification",allEntries = true),
            @CacheEvict(value = "applicationData",allEntries = true)
        }
    )
    public void editDocument(Integer documentId, MultipartFile file) throws InvalidImageFormateException, IOException {
        DocumentModel document = documentRepository.findById(documentId).orElseThrow(
                ()->new ResourceNotFoundException("Document","documentId",documentId.toString())
        );
        if(document.getDocumentStatus() == DocumentStatus.APPROVED){
            throw new SomethingWrongException("Document verification already completed. Upload not allowed.");
        }
        document.setDocumentName(file.getOriginalFilename());
        document.setDocumentUrl(fileService.uploadImage(path,file));
        document.setDocumentStatus(DocumentStatus.UPLOADED);

        documentRepository.save(document);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "documentVerification",allEntries = true),
            @CacheEvict(value = "applicationData",allEntries = true)
        }
    )
    public void reviewDocument(Integer documentId,
                               DocumentReviewRequestDto req) {
        DocumentModel document = documentRepository.findById(documentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("DocumentModel","documentId",documentId.toString())
                );

        if (req.getDocumentStatus() == DocumentStatus.REJECTED && req.getRejectionReason() == null) {
            throw new SomethingWrongException("Reject Reason Not Found !");
        }

        document.setDocumentStatus(req.getDocumentStatus());
        document.setRejectionReason(req.getRejectionReason());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "documentVerification",allEntries = true),
            @CacheEvict(value = "applicationData",allEntries = true)
        }
    )
    public void deleteDocument(Integer documentId) {
        DocumentModel document = documentRepository.findById(documentId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("DocumentModel","documentId",documentId.toString())
                );
        documentRepository.delete(document);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "documentVerification",allEntries = true),
            @CacheEvict(value = "applicationData",allEntries = true)
        }
    )
    public void finalizeVerification(Integer documentVerificationId) {
        DocumentVerificationModel verification =
                documentVerificationRepository.findById(documentVerificationId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("DocumentVerificationModel","documentVerificationId",documentVerificationId.toString())
                        );
        ApplicationModel application = verification.getApplication();
        boolean anyRejected = verification.getDocuments()
                .stream()
                .anyMatch(d -> d.getDocumentStatus() == DocumentStatus.REJECTED);

        boolean allApproved = verification.getDocuments()
                .stream()
                .allMatch(d -> d.getDocumentStatus() == DocumentStatus.APPROVED);

        if (anyRejected) {
            verification.setVerificationStatus(DocumentVerificationStatus.REJECTED);
            documentRejectMailToCandidate(application.getCandidate().getUser().getUsername(),
                    application.getCandidate().getUser().getUserEmail(),
                    application.getPosition().getPositionTitle(),
                    "Check System For More Detail !"
                    );
            return;
        }

        if (!allApproved) {
            throw new SomethingWrongException("All documents must be reviewed");
        }

        verification.setVerificationStatus(DocumentVerificationStatus.APPROVED);
        verification.setVerifiedAt(LocalDateTime.now());
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        verification.setVerifiedBy(currentUser);



        application.getApplicationStatus().setApplicationStatus(ApplicationStatus.HIRED);
        hireMailToCandidate(application.getCandidate().getUser().getUsername(),
                application.getCandidate().getUser().getUserEmail(),
                application.getPosition().getPositionTitle(),
                application.getPosition().getPositionLocation()
        );
    }

    @Override
    @Cacheable(value = "documentVerification",key = "'document_verification_status_'+#status+'_page_'+#page+'_' + 'size_'+#size+'_' + 'sortBy_'+#sortBy+'_'+'sortDir'+#sortDir")
    public PaginatedResponse<DocumentVerificationResponseDto> getAllDocumentVerificationApplications(DocumentVerificationStatus status, Integer page, Integer size, String sortBy, String sortDir) {
        logger.info("Fetching all Document Verification Applications - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Page<DocumentVerificationModel> raw =
                status == null
                        ? documentVerificationRepository.findAll(getPageable(page, size, sortBy, sortDir))
                        : documentVerificationRepository.findByVerificationStatus(status, getPageable(page, size, sortBy, sortDir));
        PaginatedResponse<DocumentVerificationResponseDto> response = getPaginatedApplications(raw);
        logger.info("Fetched {} applications ", response.getData().size());
        return response;
    }
//    documentVerification
    @Override
    @Cacheable(value = "documentVerification",key = "'document_verification_applicationId_'+#applicationId")
    public DocumentVerificationResponseDto getDocumentVerificationApplicationByApplication(Integer applicationId) {
        DocumentVerificationModel documentVerification = documentVerificationRepository.findByApplicationApplicationId(applicationId).orElseThrow(
                ()->new ResourceNotFoundException("DocumentVerificationModel","applicationId",applicationId.toString())
        );
        return convert(documentVerification);
    }



    private Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PaginatedResponse<DocumentVerificationResponseDto> getPaginatedApplications(Page<DocumentVerificationModel> pageResponse) {
        PaginatedResponse<DocumentVerificationResponseDto> response = new PaginatedResponse<>();
        response.setData(convertContent(pageResponse.getContent()));
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());
        return response;
    }

    private List<DocumentVerificationResponseDto> convertContent(List<DocumentVerificationModel> model) {
        return model.stream().map(this::convert).toList();
    }

    private DocumentVerificationResponseDto convert(DocumentVerificationModel model) {
        DocumentVerificationResponseDto dto = new DocumentVerificationResponseDto();
        dto.setApplicationId(model.getApplication().getApplicationId());
        dto.setDocumentVerificationId(model.getDocumentVerificationId());
        dto.setDocuments(model.getDocuments().stream().map(this::convert).toList());
        dto.setHrRemarks(model.getHrRemarks());
        dto.setVerificationStatus(model.getVerificationStatus());
        dto.setVerifiedAt(model.getVerifiedAt());
        dto.setVerifiedBy(model.getVerifiedBy().getUsername());

        return dto;
    }

    private DocumentResponseDto convert(DocumentModel model){
        DocumentResponseDto dto = new DocumentResponseDto();

        dto.setDocumentId(model.getDocumentId());
        dto.setDocumentName(model.getDocumentName());
        dto.setDocumentStatus(model.getDocumentStatus());
        dto.setDocumentUrl(model.getDocumentUrl());
        dto.setRejectionReason(model.getRejectionReason());

        return dto;
    }

    private void hireMailToCandidate(String candidateName,
                                 @NotEmpty(message = "Email Can't Be Empty !")
                                 @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",message = "Invalid email format!")
                                 String candidateEmail,
                                 String jobRole,
                                 String jobLocation
    ) {
        String mailBody = templateBuilder.buildCandidateHiredTemplate(candidateName,jobRole,jobLocation);

        emailService.sendMail(
                "kartikpatel7892@gmail.com",
                candidateEmail,
                "Congratulations! You’re Hired",
                mailBody
        );
    }
    private void documentRejectMailToCandidate(String candidateName,
                                 @NotEmpty(message = "Email Can't Be Empty !")
                                 @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",message = "Invalid email format!")
                                 String candidateEmail,
                                 String jobRole,
                                 String rejectReason
    ) {
        String mailBody = templateBuilder.buildDocumentRejectMail(
                "Test Company Pvt.Ltd",
                candidateName,
                jobRole,
                rejectReason,
                "Login To System !"
        );

        emailService.sendMail(
                "kartikpatel7892@gmail.com",
                candidateEmail,
                "Congratulations! You’re Hired",
                mailBody
        );
    }

}
