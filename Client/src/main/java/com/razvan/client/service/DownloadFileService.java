package com.razvan.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
public class DownloadFileService {
    private final String clientHost;
    private final String clientPort;
    private final String trackerHost;
    private final String trackerPort;
    private final Integer chunkSize;

    public DownloadFileService(
            @Value("${client.host}") String clientHost,
            @Value("${server.port}") String clientPort,
            @Value("${tracker.host}") String trackerHost,
            @Value("${tracker.port}") String trackerPort,
            @Value("${file.chunk.size}") String chunkSize
    ) {
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
        this.chunkSize = Integer.parseInt(chunkSize);
    }

    public ResponseEntity<byte[]> fetchFileMetadata(String filename) {
        RestTemplate restTemplate = new RestTemplate();
        String trackerServiceUrl = "http://" + trackerHost + ":" + trackerPort + "/file-metadata/" + filename;
        return restTemplate.getForEntity(trackerServiceUrl, byte[].class);
    }

    private Map<String, Object> extractFileMetadata(ResponseEntity<byte[]> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonContent = new String(response.getBody(), StandardCharsets.UTF_8);
            return objectMapper.readValue(jsonContent, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse file metadata", e);
        }
    }


    public void downloadChunks(String filename) {
        Map<String, Object> metadata = extractFileMetadata(fetchFileMetadata(filename));
        Integer filesize = (Integer) metadata.get("filesize");
        Integer numberOfChunks = filesize / chunkSize;
        List<String> downloadedBy = (List<String>) metadata.get("downloadedBy");

        for (int chunk = 0; chunk < numberOfChunks; chunk++) {
            String baseUrl = "http://" + downloadedBy.get(chunk % downloadedBy.size());
            String endpoint = baseUrl + "/download-chunk/" + filename + "/" + chunk;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            try {
                ResponseEntity<byte[]> response = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, byte[].class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    byte[] chunkData = response.getBody();
                    System.out.println("Successfully downloaded chunk " + chunk);
                } else {
                    System.out.println("Failed to download chunk " + chunk + ". Status code: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.err.println("Error while downloading chunk " + chunk + ": " + e.getMessage());
            }
        }

    }
}
