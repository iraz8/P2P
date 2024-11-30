package com.razvan.tracker.repository;

import com.razvan.tracker.model.ClientFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientFileRepository extends JpaRepository<ClientFile, Long> {
    List<ClientFile> findByFileName(String fileName);
}
