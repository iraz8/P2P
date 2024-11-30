package com.razvan.tracker.service;

import com.razvan.tracker.model.ClientFile;
import com.razvan.tracker.repository.ClientFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileClientsService {

    private final ClientFileRepository clientFileRepository;

    @Autowired
    public FileClientsService(ClientFileRepository clientFileRepository) {
        this.clientFileRepository = clientFileRepository;
    }

    public List<ClientFile> getClientsForFile(String fileName) {
        return clientFileRepository.findByFileName(fileName);
    }
}
