package com.example.thy.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class LocationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String locationCode;
    private String city;
    private String country;
}