package com.razvan.tracker.controller;

import com.razvan.tracker.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/tracker")
public class FileUploadController {
    private static final String METADATA_EXTENSION = "file-metadata";
    private static final String FILE_DIRECTORY = "data/file-metadata/";

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }


    @GetMapping("/file-download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) {
        File file = new File(FILE_DIRECTORY + filename + "." + METADATA_EXTENSION);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }

    // Handle file not found exception
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleFileNotFound(FileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // Define custom FileNotFoundException for handling
    public static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String message) {
            super(message);
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientHost") String clientHost,
            @RequestParam("clientPort") int clientPort) {

        String result = fileUploadService.saveFileMetadata(file, clientHost, clientPort);

        if (result.equals("File metadata saved successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/list-files")
    public String listFiles(Model model) {
        model.addAttribute("files", fileUploadService.listFiles());
        return "list-files";
    }
}
