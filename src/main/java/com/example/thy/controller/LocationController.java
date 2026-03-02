package com.example.thy.controller;

import com.example.thy.dto.LocationDTO;
import com.example.thy.service.LocationService;
import lombok.RequiredArgsConstructor; // Constructor injection için
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/")
    public ResponseEntity<List<LocationDTO>> getAll() {
        // Entity yerine LocationDTO dönüyoruz
        return ResponseEntity.ok(locationService.getAll());
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')") // Lokasyon ekleme yetkisi genelde ADMIN'dedir
    public ResponseEntity<LocationDTO> create(@RequestBody LocationDTO locationDto) {
        return ResponseEntity.ok(locationService.save(locationDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDto) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}