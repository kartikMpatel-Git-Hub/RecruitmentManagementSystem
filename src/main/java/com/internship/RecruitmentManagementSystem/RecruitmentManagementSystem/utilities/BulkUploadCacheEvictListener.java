package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Data
public class BulkUploadCacheEvictListener {
    private static final Logger log = LoggerFactory.getLogger(BulkUploadCacheEvictListener.class);
    @CacheEvict(
            value = {
                    "candidateData",
                    "bulkUploadJobData"
            },
            allEntries = true
    )
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleBulkUploadCompleted(BulkUploadCompletedEvent event) {
        log.info(
                "Cache evicted after bulk upload completion. Job ID: {}",
                event.jobId()
        );
    }
}
