package com.razvan.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.razvan.tracker.service.FileUploadService.METADATA_DIR;

@Service
public class FileListService {

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
