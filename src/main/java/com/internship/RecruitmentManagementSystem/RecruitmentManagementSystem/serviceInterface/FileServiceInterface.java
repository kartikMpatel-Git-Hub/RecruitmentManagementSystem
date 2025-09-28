package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileServiceInterface {

    String uploadImage(String path, MultipartFile file) throws IOException, InvalidImageFormateException;

    InputStream getResource(String path,String fileName)throws FileNotFoundException;
}
