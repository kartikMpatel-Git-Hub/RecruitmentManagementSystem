package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.FileServiceInterface;
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
public class FileService implements FileServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "docx");
    private static final String INVALID_IMAGE_FORMAT_MESSAGE = "Image Must Be [JPG|JPEG|PNG|PDF|DOC|DOCX] But Found ";
    private static final String FILE_SEPARATOR = File.separator;

    private final Cloudinary cloudinary;

    public FileService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException, InvalidImageFormateException {
        logger.info("Uploading file: {} to path: {}", file.getOriginalFilename(), path);
        validateInputs(path, file);

        String fileName = generateFileName(file);
        String filePath = createFilePath(path, fileName);
        createDirectoryIfNotExists(path);
        saveFile(file, filePath);

        String uploadedUrl;
        try {
            File localFile = new File(filePath);
            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf('.') + 1)
                    .toLowerCase();

            Map uploadResult;
            if (Arrays.asList("pdf", "doc", "docx").contains(extension)) {
                logger.debug("Uploading document file to Cloudinary: {}", fileName);
                uploadResult = cloudinary.uploader().upload(localFile, ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "resumes",
                        "overwrite", true,
                        "access_mode", "public"
                ));
            } else {
                logger.debug("Uploading image file to Cloudinary: {}", fileName);
                uploadResult = cloudinary.uploader().upload(localFile, ObjectUtils.asMap("resource_type", "image"));
            }

            uploadedUrl = uploadResult.get("secure_url").toString();
            logger.info("File successfully uploaded to Cloudinary: {}", uploadedUrl);

            if (localFile.delete()) {
                logger.debug("Temporary local file deleted: {}", filePath);
            } else {
                logger.warn("Failed to delete local temp file: {}", filePath);
            }

        } catch (Exception e) {
            logger.error("Cloud upload failed for file: {}. Keeping local file.", filePath, e);
            throw new IOException("Cloud upload failed: " + e.getMessage());
        }

        return uploadedUrl;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        validateResourcePath(path, fileName);

        String fullPath = path + FILE_SEPARATOR + fileName;
        logger.info("Retrieving resource from path: {}", fullPath);

        File file = new File(fullPath);
        if (!file.exists()) {
            logger.error("Resource not found at path: {}", fullPath);
            throw new FileNotFoundException("File not found: " + fullPath);
        }

        return new FileInputStream(file);
    }

    private void validateInputs(String path, MultipartFile file) throws InvalidImageFormateException {
        if (path == null || path.trim().isEmpty()) {
            logger.error("Invalid path provided");
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
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

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String[] splitName = originalFilename.split("\\.");
        String newFileName = splitName[0] + "-" + System.currentTimeMillis() + "." + splitName[1];
        logger.debug("Generated file name: {}", newFileName);
        return newFileName;
    }

    private String createFilePath(String path, String fileName) {
        return path + FILE_SEPARATOR + fileName;
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            logger.info("Creating directory: {}", path);
            if (!directory.mkdirs()) {
                logger.error("Failed to create directory: {}", path);
                throw new RuntimeException("Failed to create directory: " + path);
            }
        }
    }

    private void saveFile(MultipartFile file, String filePath) throws IOException {
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
            logger.debug("File saved locally at path: {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving file locally: {}", filePath, e);
            throw new IOException("Failed to save file: " + e.getMessage());
        }
    }

    private void validateResourcePath(String path, String fileName) {
        if (path == null || path.trim().isEmpty() || fileName == null || fileName.trim().isEmpty()) {
            logger.error("Invalid path or file name for resource retrieval");
            throw new IllegalArgumentException("Path and file name cannot be null or empty");
        }
    }
}
