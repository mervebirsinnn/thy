package com.example.thy.service;

import com.example.thy.model.Location;
import com.example.thy.model.Transportation;

import java.util.List;

public class TestDataFactory {
    public static Location createLocation(Long id, String name, String code) {
        Location loc = new Location();
        loc.setId(id);
        loc.setName(name);
        loc.setLocationCode(code);
        return loc;
    }

    public static Transportation createTransport(Location from, Location to, String type, List<Integer> days) {
        Transportation t = new Transportation();
        t.setOrigin(from);
        t.setDestination(to);
        t.setType(type);
        t.setOperatingDays(days);
        return t;
    }
}