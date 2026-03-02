package com.example.thy.repository;
import com.example.thy.model.Location;
import com.example.thy.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
    List<Transportation> findByOrigin(Location origin);
    List<Transportation> findByOriginId(Long originId);
    List<Transportation> findByOriginIdAndType(Long originId, String type);
}