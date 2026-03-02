package com.example.thy.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteResponseDTO {
    private List<TransportationDTO> steps;
    private int totalTransfers;
}