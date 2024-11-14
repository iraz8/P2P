package com.razvan.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadService {

    public static final String METADATA_DIR = "data/file-metadata";

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

        String metadataFilePath = METADATA_DIR + File.separator + file.getOriginalFilename() + ".file-metadata";

        try (FileWriter writer = new FileWriter(metadataFilePath)) {
            writer.write(new ObjectMapper().writeValueAsString(metadata));
            return "File metadata saved successfully";
        } catch (IOException e) {
            return "Failed to save metadata";
        }
    }


}
