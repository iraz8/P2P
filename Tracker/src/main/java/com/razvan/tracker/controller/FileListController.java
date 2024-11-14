package com.razvan.tracker.controller;

import com.razvan.tracker.service.FileListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileListController {

    private final FileListService fileListService;

    @Autowired
    public FileListController(FileListService fileListService) {
        this.fileListService = fileListService;
    }

    @GetMapping("/list-files")
    public String listFiles(Model model) {
        model.addAttribute("files", fileListService.listFiles());
        return "list-files";
    }
}
