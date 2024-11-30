package com.razvan.tracker.controller;

import com.razvan.tracker.service.FileDownloadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.razvan.tracker.service.FileUploadService.METADATA_EXTENSION;

@Controller
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;

    @Autowired
    public FileDownloadController(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    @GetMapping("/file-download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String filename,
            HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        int clientPort = request.getRemotePort();

        Resource resource = fileDownloadService.getFileResource(filename, clientIp, clientPort);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + METADATA_EXTENSION + "\"")
                .body(resource);
    }
}
