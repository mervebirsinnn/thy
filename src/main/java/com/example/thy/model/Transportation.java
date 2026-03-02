package com.example.thy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Transportation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Location origin;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Location destination;

    private String type;

    @ElementCollection
    @CollectionTable(name = "transportation_days", joinColumns = @JoinColumn(name = "transportation_id"))
    @Column(name = "day_of_week")
    private List<Integer> operatingDays;

    private Double price;
}

