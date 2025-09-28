package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

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

@Service
public class FileService implements FileServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png");
    private static final String INVALID_IMAGE_FORMAT_MESSAGE = "Image Must Be [JPG|JPEG|PNG] But Found ";
    private static final String FILE_SEPARATOR = File.separator;

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException, InvalidImageFormateException {
        validateInputs(path, file);

        String fileName = generateFileName(file);
        String filePath = createFilePath(path, fileName);

        createDirectoryIfNotExists(path);
        saveFile(file, filePath);

        logger.info("Successfully uploaded file: {}", fileName);
        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        validateResourcePath(path, fileName);

        String fullPath = path + FILE_SEPARATOR + fileName;
        logger.info("Retrieving resource from: {}", fullPath);

        return new FileInputStream(fullPath);
    }

    private void validateInputs(String path, MultipartFile file) throws InvalidImageFormateException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        validateFileType(file);
    }

    private void validateFileType(MultipartFile file) throws InvalidImageFormateException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new InvalidImageFormateException("Invalid file name format");
        }

        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_TYPES.contains(fileType)) {
            throw new InvalidImageFormateException(INVALID_IMAGE_FORMAT_MESSAGE + fileType);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String[] splitName = originalFilename.split("\\.");
        return splitName[0] + "-" + System.currentTimeMillis() + "." + splitName[1];
    }

    private String createFilePath(String path, String fileName) {
        return path + FILE_SEPARATOR + fileName;
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            logger.info("Creating directory: {}", path);
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + path);
            }
        }
    }

    private void saveFile(MultipartFile file, String filePath) throws IOException {
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage());
            throw new IOException("Failed to save file: " + e.getMessage());
        }
    }

    private void validateResourcePath(String path, String fileName) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
    }
}
