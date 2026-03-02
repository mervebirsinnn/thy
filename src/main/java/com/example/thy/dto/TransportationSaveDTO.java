package com.example.thy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TransportationSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String originCode;
    private String destinationCode;
    private String type;
    private List<Integer> operatingDays;
}