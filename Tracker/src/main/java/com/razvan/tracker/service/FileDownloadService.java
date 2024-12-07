package com.razvan.tracker.service;

import com.razvan.tracker.model.DownloadedFiles;
import com.razvan.tracker.repository.DownloadedFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileDownloadService {
    private final String metadataFileDirectory;
    private final String metadataFileExtension;

    private final DownloadedFilesRepository downloadedFilesRepository;

    @Autowired
    public FileDownloadService(DownloadedFilesRepository downloadedFilesRepository,
                               @Value("${metadata.file.directory}") String metadataFileDirectory,
                               @Value("${metadata.file.extension}") String metadataFileExtension) {
        this.downloadedFilesRepository = downloadedFilesRepository;
        this.metadataFileDirectory = metadataFileDirectory;
        this.metadataFileExtension = metadataFileExtension;
    }

    public Resource getFileResource(String filename, String clientIp, int clientPort) {
        File file = new File(metadataFileDirectory + File.separator + filename + "." + metadataFileExtension);
        if (!file.exists()) {
            return null;
        }

        String clientIdentifier = clientIp + ":" + clientPort;

        if (downloadedFilesRepository.findByFilenameAndDownloadedBy(filename, clientIdentifier).isEmpty()) {
            DownloadedFiles downloadedFiles = new DownloadedFiles();
            downloadedFiles.setFilename(filename);
            downloadedFiles.setDownloadedBy(clientIdentifier);
            downloadedFilesRepository.save(downloadedFiles);
        }

        return new FileSystemResource(file);
    }
}
