package com.razvan.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class TrackerFileListController {

    private final RestTemplate restTemplate;

    @Value("${tracker.host}")
    private String trackerHost;

    @Value("${tracker.port}")
    private String trackerPort;

    public TrackerFileListController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/tracker-file-list")
    public String getTrackerFileList(Model model) {
        String trackerUrl = String.format("http://%s:%s/api/list-files", trackerHost, trackerPort);
        List<?> files = restTemplate.getForObject(trackerUrl, List.class);
        model.addAttribute("files", files);
        return "tracker-file-list";
    }

    @GetMapping("/download-file")
    public String downloadFile(@RequestParam("fileName") String fileName, Model model) {
        String trackerUrl = String.format("http://%s:%s/get-file-clients?fileName=%s", trackerHost, trackerPort, fileName);
        List<?> clientInfo = restTemplate.getForObject(trackerUrl, List.class);
        model.addAttribute("clientInfo", clientInfo);
        return "file-clients";
    }

}
