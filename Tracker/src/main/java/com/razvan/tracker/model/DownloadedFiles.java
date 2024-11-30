package com.razvan.tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "downloadedFiles", uniqueConstraints = @UniqueConstraint(columnNames = {"filename", "downloadedBy", "clientPort"}))
public class DownloadedFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String downloadedBy; // Client IP
    private Integer clientPort;  // Client port
    private Integer numberOfChunks;
}
