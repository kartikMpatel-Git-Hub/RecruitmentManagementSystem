package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.FailedProcessException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.FileServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "docx");
    private static final String INVALID_IMAGE_FORMAT_MESSAGE = "Image Must Be [JPG|JPEG|PNG|PDF|DOC|DOCX] But Found ";
    private static final String FILE_SEPARATOR = File.separator;

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws InvalidImageFormateException {
        logger.info("Image upload started to Cloudinary");
        logger.debug("Image upload request: filename={}, size={}, contentType={}",
                file != null ? file.getOriginalFilename() : null,
                file != null ? file.getSize() : null,
                file != null ? file.getContentType() : null);
        validateInputs(file);
        try{
            Map uploadedResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder","oss/image",
                            "resource_type","image"
                    )
            );

            logger.info("Image uploaded successfully to Cloudinary");
            logger.debug("Cloudinary upload response: {}", uploadedResult);

            return uploadedResult.get("secure_url").toString();
        } catch (Exception e) {
            logger.error("Image upload failed to Cloudinary", e);
            throw new FailedProcessException("image upload failed");
        }
    }
    private void validateInputs(MultipartFile file) throws InvalidImageFormateException {
        if (file == null || file.isEmpty()) {
            logger.error("Invalid file provided");
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        validateFileType(file);
    }

    private void validateFileType(MultipartFile file) throws InvalidImageFormateException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            logger.error("Invalid file name format: {}", originalFilename);
            throw new InvalidImageFormateException("Invalid file name format");
        }

        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_TYPES.contains(fileType)) {
            logger.error("Invalid file type: {}", fileType);
            throw new InvalidImageFormateException(INVALID_IMAGE_FORMAT_MESSAGE + fileType);
        }

        logger.debug("File type validated: {}", fileType);
    }
}
