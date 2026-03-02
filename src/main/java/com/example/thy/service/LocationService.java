package com.example.thy.service;

import com.example.thy.dto.LocationDTO;
import com.example.thy.mapper.ApplicationMapper;
import com.example.thy.model.Location;
import com.example.thy.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final ApplicationMapper mapper;

    public List<LocationDTO> getAll() {
        List<Location> locations = locationRepository.findAll();
        return mapper.mapList(locations, LocationDTO.class);
    }

    public LocationDTO save(LocationDTO locationDto) {
        Location location = mapper.map(locationDto, Location.class);
        Location saved = locationRepository.save(location);
        return mapper.map(saved, LocationDTO.class);
    }

    public LocationDTO findById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return mapper.map(location, LocationDTO.class);
    }

    @CacheEvict(value = "routes", allEntries = true)
    public LocationDTO updateLocation(Long id, LocationDTO updatedDto) {
        return locationRepository.findById(id).map(location -> {
            location.setName(updatedDto.getName());
            location.setLocationCode(updatedDto.getLocationCode());
            location.setCity(updatedDto.getCity());
            location.setCountry(updatedDto.getCountry());

            Location saved = locationRepository.save(location);
            return mapper.map(saved, LocationDTO.class);
        }).orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
    }

    @CacheEvict(value = "routes", allEntries = true)
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}