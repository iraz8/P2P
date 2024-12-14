package com.razvan.client.controller;

import com.razvan.client.service.UploadFileMetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;


@RestController
public class DownloadFileController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/download-chunk/{filename}/{chunk}")
    public ResponseEntity<byte[]> downloadChunk(
            @PathVariable("filename") String filename,
            @PathVariable("chunk") int chunk) {

        String clientStoragePath = new File("").getAbsolutePath() + File.separator + "clientStorage";
        String folderName = getLocalIpAddress() + "_" + serverPort + "_" + UploadFileMetadataService.getFileNameWithoutExtension(filename);
        String chunkFileName = filename + ".part" + chunk;

        File chunkFile = new File(clientStoragePath + File.separator + folderName + File.separator + chunkFileName);

        if (!chunkFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            byte[] chunkData = Files.readAllBytes(chunkFile.toPath());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(chunkData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public static String getLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unable to retrieve IP";
        }
    }
}
