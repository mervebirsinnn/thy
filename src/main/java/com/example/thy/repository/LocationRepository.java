package com.example.thy.repository;

import com.example.thy.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByName(String name);
    Optional<Location> findByLocationCode(String locationCode);
}
