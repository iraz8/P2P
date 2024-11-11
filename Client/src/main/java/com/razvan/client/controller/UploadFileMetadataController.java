package com.razvan.client.controller;

import com.razvan.client.service.UploadFileMetadataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/new-file-metadata")
public class UploadFileMetadataController {

    private final UploadFileMetadataService uploadFileMetadataService;

    @Autowired
    public UploadFileMetadataController(UploadFileMetadataService uploadFileMetadataService) {
        this.uploadFileMetadataService = uploadFileMetadataService;
    }

    @GetMapping("/upload-details")
    public String showUploadForm(Model model) {
        if (!model.containsAttribute("fileDetails")) {
            model.addAttribute("fileDetails", "No file details available yet.");
        }
        return "new-file-metadata-upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/new-file-metadata/upload-details";
        }

        String fileDetails = uploadFileMetadataService.obtainFileDetails(file, request);

        redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        redirectAttributes.addFlashAttribute("fileDetails", fileDetails);

        return "redirect:/new-file-metadata/upload-details";
    }
}
