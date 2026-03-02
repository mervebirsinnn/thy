package com.example.thy.controller;

import com.example.thy.dto.TransportationDTO;
import com.example.thy.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/search")
    public ResponseEntity<List<List<TransportationDTO>>> search(
            @RequestParam Long originId,
            @RequestParam Long destinationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<List<TransportationDTO>> routes = routeService.findRoutes(originId, destinationId, date);
        return ResponseEntity.ok(routes);
    }
}