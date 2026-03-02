package com.example.thy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TransportationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String originName;
    private String originCode;
    private String destinationName;
    private String destinationCode;
    private String type;
    private List<Integer> operatingDays;
}