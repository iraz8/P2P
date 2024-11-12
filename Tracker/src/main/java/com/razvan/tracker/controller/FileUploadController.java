package com.razvan.tracker.controller;

import com.razvan.tracker.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/tracker")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
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
