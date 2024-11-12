package com.razvan.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileUploadService {

    private static final String METADATA_DIR = "data/file-metadata";

    public String saveFileMetadata(MultipartFile file, String clientHost, int clientPort) {
        File metadataDir = new File(METADATA_DIR);
        if (!metadataDir.exists()) {
            metadataDir.mkdirs();
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("filename", file.getOriginalFilename());
        metadata.put("filesize", file.getSize());
        metadata.put("fileformat", file.getContentType());
        metadata.put("clientHost", clientHost);
        metadata.put("clientPort", clientPort);

        String metadataFilePath = METADATA_DIR + "/" + file.getOriginalFilename() + ".file-metadata";

        try (FileWriter writer = new FileWriter(metadataFilePath)) {
            writer.write(new ObjectMapper().writeValueAsString(metadata));
            return "File metadata saved successfully";
        } catch (IOException e) {
            return "Failed to save metadata";
        }
    }

    public List<Map<String, Object>> listFiles() {
        List<Map<String, Object>> files = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        File metadataDir = new File(METADATA_DIR);
        if (metadataDir.exists()) {
            for (File file : metadataDir.listFiles((dir, name) -> name.endsWith(".file-metadata"))) {
                try {
                    Map<String, Object> metadata = mapper.readValue(file, Map.class);
                    files.add(metadata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return files;
    }
}
