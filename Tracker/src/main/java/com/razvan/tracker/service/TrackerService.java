package com.razvan.tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.razvan.tracker.model.TrackerEntity;
import com.razvan.tracker.model.TrackerRequest;
import com.razvan.tracker.repository.TrackerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TrackerService {

    private final String metadataFileDirectory;
    private final String metadataFileExtension;

    private final TrackerRepository trackerRepository;

    public TrackerService(TrackerRepository trackerRepository,
                          @Value("${metadata.file.directory}") String metadataFileDirectory,
                          @Value("${metadata.file.extension}") String metadataFileExtension) {
        this.trackerRepository = trackerRepository;
        this.metadataFileDirectory = metadataFileDirectory;
        this.metadataFileExtension = metadataFileExtension;
    }

    public void saveDownload(TrackerRequest trackerRequest) {
        TrackerEntity entity = new TrackerEntity();
        entity.setFileName(trackerRequest.getFileName());
        entity.setClientIP(trackerRequest.getClientIP());
        entity.setClientPort(trackerRequest.getClientPort());
        trackerRepository.save(entity);
    }

    public void updateMetadataFile(TrackerRequest trackerRequest) {
        Path metadataPath = Paths.get(metadataFileDirectory, trackerRequest.getFileName() + "." + metadataFileExtension);
        if (Files.exists(metadataPath)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode metadataJson = (ObjectNode) objectMapper.readTree(Files.newInputStream(metadataPath));
                ArrayNode downloadedByArray = metadataJson.withArray("downloadedBy");
                String newEntry = trackerRequest.getClientIP() + ":" + trackerRequest.getClientPort();

                boolean entryExists = false;
                for (JsonNode node : downloadedByArray) {
                    if (node.asText().equals(newEntry)) {
                        entryExists = true;
                        break;
                    }
                }

                if (!entryExists) {
                    downloadedByArray.add(newEntry);
                }
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(Files.newOutputStream(metadataPath), metadataJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
