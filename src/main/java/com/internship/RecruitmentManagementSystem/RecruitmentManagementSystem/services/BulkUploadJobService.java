package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.BulkJobResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.job.JobStatusResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.JobStatus;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadRowResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadJobRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.BulkUploadRowResultRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.BulkUploadJobServiceInterface;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkUploadJobService implements BulkUploadJobServiceInterface {

    private final BulkUploadJobRepository bulkUploadJobRepository;
    private final BulkUploadRowResultRepository bulkUploadRowResultRepository;
    private final BulkCandidateService candidateService;
    private final UserRepository userRepository;

    @Override
    public Integer processBulkUploadJob(MultipartFile file,Integer uploadedById) {
        try{
            BulkUploadJob job = new BulkUploadJob();
            job.setFileName(file.getOriginalFilename());
            job.setStatus(JobStatus.PENDING);
            job.setStartedAt(LocalDateTime.now());

            UserModel uploadedBy = userRepository.findById(uploadedById).orElseThrow(
                    ()->new ResourceNotFoundException("User", "id", uploadedById.toString())
            );
            job.setUploadedBy(uploadedBy);
            bulkUploadJobRepository.save(job);
            processAsync(job, file);

            return job.getJobId();

        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public JobStatusResponseDto getJobStatus(Integer jobId) {

        BulkUploadJob job = bulkUploadJobRepository.findById(jobId).orElseThrow(
                () -> new ResourceNotFoundException("BulkUploadJob", "id", jobId.toString())
        );
        JobStatusResponseDto res = new JobStatusResponseDto();
        res.setJobId(job.getJobId());
        res.setStatus(job.getStatus());
        res.setTotalRows(job.getTotalRows());
        res.setSuccessRows(job.getSuccessRows());
        res.setFailedRows(job.getFailedRows());
        return res;
    }

    @Override
    public List<BulkJobResponseDto> getAllBulkEntries() {
        List<BulkUploadJob> jobs = bulkUploadJobRepository.findAll();
        if(!jobs.isEmpty()){
            return jobs.stream().map(this::convertor).toList();
        }
        return List.of();
    }

    private BulkJobResponseDto convertor(BulkUploadJob entity) {
        BulkJobResponseDto dto = new BulkJobResponseDto();
        dto.setJobId(entity.getJobId());
        dto.setFileName(entity.getFileName());
        dto.setStatus(entity.getStatus());
        dto.setTotalRows(entity.getTotalRows());
        dto.setSuccessRows(entity.getSuccessRows());
        dto.setFailedRows(entity.getFailedRows());
        dto.setStartedAt(entity.getStartedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setUploadedById(entity.getUploadedBy().getUserId());
        return dto;
    }

    @Async
    public void processAsync(BulkUploadJob job, MultipartFile file) {
        job.setStatus(JobStatus.IN_PROGRESS);
        bulkUploadJobRepository.save(job);

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int total = sheet.getPhysicalNumberOfRows() - 1;
            job.setTotalRows(total);
            int success = 0;
            int failed = 0;

            for (int i = 1; i <= total; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                BulkUploadRowResult rowResult = new BulkUploadRowResult();
                rowResult.setJob(job);
                rowResult.setRowNum(i + 1);

                try {
                    candidateService.processSingleRow(row);
                    rowResult.setSuccess(true);
                    success++;
                } catch (Exception e) {
                    rowResult.setSuccess(false);
                    rowResult.setErrorMessage(e.getMessage());
                    failed++;
                }

                bulkUploadRowResultRepository.save(rowResult);
            }

            job.setSuccessRows(success);
            job.setFailedRows(failed);
            List<BulkUploadRowResult> allRows = bulkUploadRowResultRepository.findByJob(job);

//            String successFile = ExcelExportUtil.generateSuccessExcel(job, allRows, outputBasePath);
//            String errorFile = ExcelExportUtil.generateErrorExcel(jobId, allRows, outputBasePath);
//
//            job.setSuccessFilePath(successFile);
//            job.setErrorFilePath(errorFile);
            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            bulkUploadJobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            bulkUploadJobRepository.save(job);
        }
    }
}
