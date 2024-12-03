package com.razvan.tracker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackerRequest {
    private String fileName;
    private String clientIP;
    private String clientPort;
}
