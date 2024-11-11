package com.razvan.client.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileMetadataService {

    @Value("${tracker.host}")
    private String serverHost;

    public String obtainFileDetails(MultipartFile file, HttpServletRequest request) {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String fileFormat = file.getContentType();

        String clientHost = request.getRemoteAddr();
        int clientPort = request.getRemotePort();

        return String.format("""
                File metadata received!\n
                File Name: %s\n
                File Size: %d bytes\n
                File Format: %s\n
                Client Host: %s:%d\n
                Server Host: %s\n
                """, fileName, fileSize, fileFormat, clientHost, clientPort, serverHost);

    }
}