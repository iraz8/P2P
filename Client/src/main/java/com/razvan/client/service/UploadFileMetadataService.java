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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class UploadFileMetadataService {
    private static final Logger logger = LoggerFactory.getLogger(UploadFileMetadataService.class);
    private final String clientPort;
    private final String trackerUrl;

    public UploadFileMetadataService(@Value("${server.port}") String clientPort,
                                     @Value("${tracker.host}") String trackerHost,
                                     @Value("${tracker.port}") String trackerPort) {
        this.clientPort = clientPort;
        this.trackerUrl = "http://" + trackerHost + ":" + trackerPort + "/uploadFile";
        logger.info("Tracker URL set to: {}", this.trackerUrl);
    }

    public String uploadFileToTracker(MultipartFile file) throws IOException {
        String clientHost;
        try {
            clientHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            clientHost = "127.0.0.1";
        }

        logger.info("Uploading file: {}", file.getOriginalFilename());

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

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(trackerUrl, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return "Upload failed: " + e.getMessage();
        }
    }
}