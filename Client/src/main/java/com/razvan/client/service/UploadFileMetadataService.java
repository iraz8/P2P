package com.razvan.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class UploadFileMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileMetadataService.class);

    private final Integer chunkSize;
    private final String clientPort;
    private final String trackerUrl;

    public UploadFileMetadataService(
            @Value("${server.port}") String clientPort,
            @Value("${tracker.host}") String trackerHost,
            @Value("${tracker.port}") String trackerPort,
            @Value("${file.chunk.size}") String chunkSize
    ) {
        this.clientPort = clientPort;
        this.trackerUrl = "http://" + trackerHost + ":" + trackerPort + "/uploadFile";
        this.chunkSize = Integer.parseInt(chunkSize);
    }

    public String uploadFileToTracker(MultipartFile file) throws IOException {
        String clientHost = getClientHost();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(file, clientHost);
        return sendRequestToTracker(requestEntity);
    }

    public String saveFileToClientStorage(MultipartFile file) throws IOException {
        String clientHost = getClientHost();
        String originalFileName = file.getOriginalFilename();
        String fileNameWithoutExtension = getFileNameWithoutExtension(originalFileName);
        String clientStoragePath = constructStoragePath(clientHost, fileNameWithoutExtension);

        createDirectory(clientStoragePath);
        saveFileInChunks(file, clientStoragePath, originalFileName);

        return clientStoragePath;
    }

    private String getClientHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createMultipartRequest(MultipartFile file, String clientHost) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("clientHost", clientHost);
        body.add("clientPort", clientPort);

        return new HttpEntity<>(body, headers);
    }

    private String sendRequestToTracker(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(trackerUrl, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return "Upload failed: " + e.getMessage();
        }
    }

    public static String getFileNameWithoutExtension(String fileName) {
        return fileName != null ? fileName.replaceFirst("[.][^.]+$", "") : "unknown_file";
    }

    private String constructStoragePath(String clientHost, String fileNameWithoutExtension) {
        return new File("").getAbsolutePath() + File.separator + "clientStorage" + File.separator + clientHost + "_" + clientPort + "_" + fileNameWithoutExtension;
    }

    private void createDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create directory: " + path);
        }
    }

    private void saveFileInChunks(MultipartFile file, String storagePath, String originalFileName) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            int chunkIndex = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                File chunkFile = new File(storagePath, originalFileName + ".part" + chunkIndex++);
                try (OutputStream outputStream = new FileOutputStream(chunkFile)) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
