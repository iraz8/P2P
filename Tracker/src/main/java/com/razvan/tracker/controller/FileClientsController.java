package com.razvan.tracker.controller;

import com.razvan.tracker.model.ClientFile;
import com.razvan.tracker.service.FileClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileClientsController {

    private final FileClientsService fileClientsService;

    @Autowired
    public FileClientsController(FileClientsService fileClientsService) {
        this.fileClientsService = fileClientsService;
    }

    @GetMapping("/get-file-clients")
    public List<ClientFile> getFileClients(@RequestParam("fileName") String fileName) {
        return fileClientsService.getClientsForFile(fileName);
    }

}
