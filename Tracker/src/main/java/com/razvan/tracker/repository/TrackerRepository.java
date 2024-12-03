package com.razvan.tracker.repository;

import com.razvan.tracker.model.TrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackerRepository extends JpaRepository<TrackerEntity, Long> {
}
