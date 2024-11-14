package com.razvan.tracker.repository;

import com.razvan.tracker.model.DownloadedFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DownloadedFilesRepository extends JpaRepository<DownloadedFiles, Long> {
    Optional<DownloadedFiles> findByFilenameAndDownloadedBy(String filename, String downloadedBy);
}
