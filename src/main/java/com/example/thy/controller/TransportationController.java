package com.example.thy.controller;

import com.example.thy.dto.TransportationDTO;
import com.example.thy.dto.TransportationSaveDTO;
import com.example.thy.model.Transportation;
import com.example.thy.service.TransportationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transportations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransportationController {

    private final TransportationService transportationService;

    @GetMapping("/")
    public ResponseEntity<List<TransportationDTO>> getAll() {
        return ResponseEntity.ok(transportationService.findAllAsDTO());
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportationDTO> create(@RequestBody TransportationSaveDTO saveDTO) {
        // Controller artık Entity'yi tanımıyor, sadece veri taşıyıcıyı (DTO) servise paslıyor
        TransportationDTO result = transportationService.save(saveDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/origin/{originId}")
    public ResponseEntity<List<TransportationDTO>> getByOrigin(@PathVariable Long originId) {
        List<TransportationDTO> results = transportationService.getTransportationsByOrigin(originId);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transportationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportationDTO> update(@PathVariable Long id, @RequestBody TransportationSaveDTO updateDTO) {
        TransportationDTO result = transportationService.update(id, updateDTO);
        return ResponseEntity.ok(result);
    }
}