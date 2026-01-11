package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import org.springframework.scheduling.annotation.Async;

public interface AsyncServiceInterface {
    @Async
    void processAsync(BulkUploadJob job, byte[] fileByte,String fileName);
}
