package com.razvan.client.controller;

import com.razvan.client.service.UploadFileMetadataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


@RestController
public class DownloadFileController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/download-chunk/{filename}/{chunk}")
    public ResponseEntity<byte[]> downloadChunk(
            @PathVariable("filename") String filename,
            @PathVariable("chunk") int chunk,
            HttpServletRequest request) {

        String clientStoragePath = new File("").getAbsolutePath() + File.separator + "clientStorage";
        String folderName = request.getRemoteAddr() + "_" + serverPort + "_" + UploadFileMetadataService.getFileNameWithoutExtension(filename);
        String chunkFileName = filename + ".part" + chunk;

        File chunkFile = new File(clientStoragePath + File.separator + folderName + File.separator + chunkFileName);

        if (!chunkFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            byte[] fileContent = Files.readAllBytes(chunkFile.toPath());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + chunkFileName + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
