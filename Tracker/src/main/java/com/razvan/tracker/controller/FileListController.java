package com.razvan.tracker.controller;

import com.razvan.tracker.service.FileListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import java.util.List;

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

@RestController
class FileListApiController {

    private final FileListService fileListService;

    @Autowired
    public FileListApiController(FileListService fileListService) {
        this.fileListService = fileListService;
    }

    @GetMapping("/api/list-files")
    public List<?> listFilesApi() {
        return fileListService.listFiles();
    }
}
