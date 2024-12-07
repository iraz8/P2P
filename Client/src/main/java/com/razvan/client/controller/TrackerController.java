package com.razvan.client.controller;

import com.razvan.client.model.TrackerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/tracker")
public class TrackerController {

    private final String trackerHost;
    private final String trackerPort;
    private final RestTemplate restTemplate;

    public TrackerController(
            @Value("${tracker.host}") String trackerHost,
            @Value("${tracker.port}") String trackerPort) {
        this.trackerHost = trackerHost;
        this.trackerPort = trackerPort;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/save-download")
    public ResponseEntity<Void> saveDownload(@RequestBody TrackerRequest trackerRequest) {
        String trackerServiceUrl = "http://" + trackerHost + ":" + trackerPort + "/tracker/save-download";
        restTemplate.postForObject(trackerServiceUrl, trackerRequest, Void.class);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-metadata/{filename}")
    public ResponseEntity<byte[]> downloadMetadata(@PathVariable String filename) {
        RestTemplate restTemplate = new RestTemplate();
        String trackerServiceUrl = "http://" + trackerHost + ":" + trackerPort + "/file-metadata/" + filename;
        ResponseEntity<byte[]> response = restTemplate.getForEntity(trackerServiceUrl, byte[].class);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "-metadata.json\"")
                .body(response.getBody());
    }

}
