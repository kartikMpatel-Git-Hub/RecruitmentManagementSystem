package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.documentVerification.DocumentReviewRequestDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.DocumentVerificationStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.DocumentVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document-verification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentVerificationController {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentVerificationController.class);

    private final DocumentVerificationService documentVerificationService;

    @PostMapping("/{applicationId}/upload")
    @PreAuthorize("hasAnyRole('CANDIDATE')")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Integer applicationId,
            @RequestParam("document") MultipartFile file
    ) throws Exception, InvalidImageFormateException {

        log.info("Uploading document for applicationId={}", applicationId);

        documentVerificationService.uploadDocument(applicationId, file);

        log.info("Document uploaded successfully for applicationId={}", applicationId);

        return new ResponseEntity<>(
                "Document uploaded successfully",
                HttpStatus.CREATED
        );
    }


    @PutMapping("/document/{documentId}")
    @PreAuthorize("hasAnyRole('CANDIDATE')")
    public ResponseEntity<?> editDocument(
            @PathVariable Integer documentId,
            @RequestParam("document") MultipartFile file
    ) throws Exception, InvalidImageFormateException {

        log.info("Edit document for documentId={}", documentId);

        documentVerificationService.editDocument(documentId, file);

        log.info("Document Edited successfully for documentId={}", documentId);

        return new ResponseEntity<>(
                "Document uploaded successfully",
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/document/{documentId}/review")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> reviewDocument(
            @PathVariable Integer documentId,
            @RequestBody @Valid DocumentReviewRequestDto requestDto
    ) {

        log.info("Reviewing documentId={} with status={}",
                documentId, requestDto.getDocumentStatus());

        documentVerificationService.reviewDocument(documentId, requestDto);

        log.info("Document reviewed successfully documentId={}", documentId);

        return new ResponseEntity<>(
                "Document reviewed successfully",
                HttpStatus.OK
        );
    }

    @DeleteMapping("/document/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','CANDIDATE')")
    public ResponseEntity<?> deleteDocument(
            @PathVariable Integer documentId
    ) {

        log.info("Deleting documentId={}", documentId);

        documentVerificationService.deleteDocument(documentId);

        log.info("Deleted documentId={}", documentId);

        return new ResponseEntity<>(
                "Document deleted successfully",
                HttpStatus.OK
        );
    }

    @PatchMapping("/{documentVerificationId}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> finalizeVerification(
            @PathVariable Integer documentVerificationId
    ) {

        log.info("Finalizing document verificationId={}",
                documentVerificationId);

        documentVerificationService.finalizeVerification(documentVerificationId);

        log.info("Document verification finalized verificationId={}",
                documentVerificationId);

        return new ResponseEntity<>(
                "Document verification finalized successfully",
                HttpStatus.OK
        );
    }

    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public  ResponseEntity<?> getDocumentVerificationApplication(@PathVariable Integer applicationId){
        log.info("Fetching Document Verification application by id : {}",applicationId);
        var res = documentVerificationService.getDocumentVerificationApplicationByApplication(applicationId);
        log.info("Fetched Document Verification applications by id : {}",applicationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<?> getDocumentVerificationApplications(
            @RequestParam(required = false) DocumentVerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "documentVerificationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching Document Verification application ");
        var res = documentVerificationService.getAllDocumentVerificationApplications(status,page,size,sortBy,sortDir);
        log.info("Fetched Document Verification applications");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
