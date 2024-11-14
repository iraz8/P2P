package com.razvan.tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "downloadedFiles", uniqueConstraints = @UniqueConstraint(columnNames = {"filename", "downloadedBy"}))
public class DownloadedFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String downloadedBy;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public void setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
