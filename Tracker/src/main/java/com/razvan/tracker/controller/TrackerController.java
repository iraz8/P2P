package com.razvan.tracker.controller;

import com.razvan.tracker.model.TrackerRequest;
import com.razvan.tracker.service.TrackerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracker")
public class TrackerController {

    private final TrackerService trackerService;

    public TrackerController(TrackerService trackerService) {
        this.trackerService = trackerService;
    }

    @PostMapping("/save-download")
    public ResponseEntity<Void> saveDownload(@RequestBody TrackerRequest trackerRequest) {
        trackerService.saveDownload(trackerRequest);
        return ResponseEntity.ok().build();
    }
}
