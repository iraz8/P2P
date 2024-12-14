package com.razvan.client.controller;

import com.razvan.client.exception.NoSeedersException;
import com.razvan.client.model.FileMetadata;
import com.razvan.client.service.DownloadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class TrackerFileListController {

    private final RestTemplate restTemplate;
    private final String trackerHost;
    private final String trackerPort;
    private final DownloadFileService downloadFileService;

    public TrackerFileListController(
            @Value("${tracker.host}") String trackerHost,
            @Value("${tracker.port}") String trackerPort,
            DownloadFileService downloadFileService
    ) {
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
        this.restTemplate = new RestTemplate();
        this.downloadFileService = downloadFileService;
    }

    @GetMapping("/tracker-file-list")
    public String getTrackerFileList(Model model) {
        String trackerUrl = String.format("http://%s:%s/api/list-files", trackerHost, trackerPort);
        List<FileMetadata> files = restTemplate.getForObject(trackerUrl, List.class);
        model.addAttribute("files", files);
        return "tracker-file-list";
    }

    @GetMapping("/download-file")
    public String downloadFile(@RequestParam("fileName") String fileName, Model model) {
        String trackerUrl = String.format("http://%s:%s/get-file-clients?fileName=%s", trackerHost, trackerPort, fileName);
        List<?> clientInfo = restTemplate.getForObject(trackerUrl, List.class);
        model.addAttribute("clientInfo", clientInfo);

        downloadFileService.downloadChunks(fileName);

        return "redirect:/download-complete";
    }

    @ExceptionHandler(NoSeedersException.class)
    public String handleNoSeedersException(NoSeedersException ex, Model model) {
        model.addAttribute("message", "There are no seeders");
        return "no-seeders";
    }

    @GetMapping("/download-complete")
    public String showDownloadCompletePage(Model model) {
        model.addAttribute("message", "Completed download");
        return "download-complete";
    }

}
