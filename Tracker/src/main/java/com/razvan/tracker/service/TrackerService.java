package com.razvan.tracker.service;

import com.razvan.tracker.model.TrackerEntity;
import com.razvan.tracker.model.TrackerRequest;
import com.razvan.tracker.repository.TrackerRepository;
import org.springframework.stereotype.Service;

@Service
public class TrackerService {

    private final TrackerRepository trackerRepository;

    public TrackerService(TrackerRepository trackerRepository) {
        this.trackerRepository = trackerRepository;
    }

    public void saveDownload(TrackerRequest trackerRequest) {
        TrackerEntity entity = new TrackerEntity();
        entity.setFileName(trackerRequest.getFileName());
        entity.setClientIP(trackerRequest.getClientIP());
        entity.setClientPort(trackerRequest.getClientPort());
        trackerRepository.save(entity);
    }
}
