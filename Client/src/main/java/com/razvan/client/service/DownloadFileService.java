package com.razvan.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razvan.client.controller.DownloadFileController;
import com.razvan.client.exception.NoSeedersException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String clientStoragePath = new File("").getAbsolutePath() + File.separator + "clientStorage";
        Map<String, Object> metadata = extractFileMetadata(fetchFileMetadata(filename));
        Integer filesize = (Integer) metadata.get("filesize");
        int numberOfChunks = calculateNumberOfChunks(filesize);
        List<String> downloadedBy = getDownloadedByPeers(metadata);

        if (downloadedBy.isEmpty()) {
            throw new NoSeedersException();
        }

        downloadAllChunks(filename, clientStoragePath, numberOfChunks, downloadedBy);
        mergeChunks(filename, clientStoragePath, numberOfChunks);
    }

    private int calculateNumberOfChunks(Integer filesize) {
        return (int) Math.ceil((double) filesize / chunkSize);
    }

    private List<String> getDownloadedByPeers(Map<String, Object> metadata) {
        return ((List<String>) metadata.get("downloadedBy"))
                .stream()
                .filter(ip -> !ip.equals(clientHost + ":" + clientPort))
                .toList();
    }

    private void downloadAllChunks(String filename, String clientStoragePath, int numberOfChunks, List<String> downloadedBy) {
        for (int chunk = 0; chunk < numberOfChunks; chunk++) {
            String baseUrl = "http://" + downloadedBy.get(chunk % downloadedBy.size());
            String endpoint = baseUrl + "/download-chunk/" + filename + "/" + chunk;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            try {
                ResponseEntity<byte[]> response = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, byte[].class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    Path storagePath = Paths.get(clientStoragePath, getChunkedFilename(filename, chunk));
                    Files.createDirectories(storagePath.getParent());
                    Files.write(storagePath, response.getBody());
                }
            } catch (Exception e) {
                System.err.println("Error while downloading chunk " + chunk + ": " + e.getMessage());
            }
        }
    }

    private void mergeChunks(String filename, String clientStoragePath, int numberOfChunks) {
        String downloadsPath = clientStoragePath + File.separator + "Downloads";

        try {
            Path mergedFilePath = Paths.get(downloadsPath, filename);
            Files.createDirectories(mergedFilePath.getParent());

            try (OutputStream mergedFileStream = new BufferedOutputStream(Files.newOutputStream(mergedFilePath))) {
                for (int chunk = 0; chunk < numberOfChunks; chunk++) {
                    Path chunkPath = Paths.get(clientStoragePath, getChunkedFilename(filename, chunk));
                    if (Files.exists(chunkPath)) {
                        Files.copy(chunkPath, mergedFileStream);
                    } else {
                        System.err.println("Missing chunk " + chunk + " for file " + filename);
                    }
                }
            }

            System.out.println("File " + filename + " successfully merged into " + mergedFilePath);
        } catch (IOException e) {
            System.err.println("Failed to merge chunks for file " + filename + ": " + e.getMessage());
        }
    }

    private String getChunkedFilename(String filename, int chunkNr) {
        return DownloadFileController.getLocalIpAddress() + "_" + clientPort + "_" + UploadFileMetadataService.getFileNameWithoutExtension(filename) + File.separator + filename + ".part" + chunkNr;
    }

    @GetMapping("/no-seeders")
    public String showNoSeedersPage(Model model) {
        model.addAttribute("message", "There are no seeders");
        return "no-seeders";
    }
}
