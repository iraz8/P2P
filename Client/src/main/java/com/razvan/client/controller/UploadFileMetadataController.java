package com.razvan.client.controller;

import com.razvan.client.service.UploadFileMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/new-file-metadata")
public class UploadFileMetadataController {

    private final UploadFileMetadataService uploadFileMetadataService;
    private static final String FILE_DETAILS_ATTRIBUTE = "fileDetails";

    @Autowired
    public UploadFileMetadataController(UploadFileMetadataService uploadFileMetadataService) {
        this.uploadFileMetadataService = uploadFileMetadataService;
    }

    @GetMapping("/upload-details")
    public String uploadFile(Model model) {
        if (!model.containsAttribute(FILE_DETAILS_ATTRIBUTE)) {
            model.addAttribute(FILE_DETAILS_ATTRIBUTE, "No file details available yet.");
        }
        return "new-file-metadata-upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            String message = uploadFileMetadataService.uploadFileToTracker(file);
            String filePath = uploadFileMetadataService.saveFileToClientStorage(file);
            model.addAttribute("message", "File uploaded successfully to: " + message + "File saved in clientStorage folder: " + filePath);
        } catch (IOException e) {
            model.addAttribute("message", "File upload failed: " + e.getMessage());
        }
        return "metadata-upload-success";
    }
}
