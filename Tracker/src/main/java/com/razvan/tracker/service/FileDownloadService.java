package com.razvan.tracker.service;

import com.razvan.tracker.model.DownloadedFiles;
import com.razvan.tracker.repository.DownloadedFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileDownloadService {
    private static final String METADATA_EXTENSION = ".file-metadata";
    private static final String FILE_DIRECTORY = "data/file-metadata/";

    private final DownloadedFilesRepository downloadedFilesRepository;

    @Autowired
    public FileDownloadService(DownloadedFilesRepository downloadedFilesRepository) {
        this.downloadedFilesRepository = downloadedFilesRepository;
    }

    public Resource getFileResource(String filename, String clientIp, int clientPort) {
        File file = new File(FILE_DIRECTORY + File.separator + filename + METADATA_EXTENSION);
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
