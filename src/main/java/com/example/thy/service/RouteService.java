package com.example.thy.service;

import com.example.thy.mapper.ApplicationMapper;
import com.example.thy.model.Transportation;
import com.example.thy.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.example.thy.dto.TransportationDTO;
@Service
@RequiredArgsConstructor
public class RouteService {


    private final TransportationRepository transportationRepository;
    private final ApplicationMapper mapper;
    @Cacheable(value = "routes", key = "{#originId, #destinationId, #travelDate}")
    public List<List<TransportationDTO>> findRoutes(Long originId, Long destinationId, LocalDate travelDate) {
        System.out.println("===> BFS Algoritması Çalışıyor (Veritabanına gidiliyor)...");
        List<List<Transportation>> allValidRoutes = new ArrayList<>();
        Queue<List<Transportation>> queue = new LinkedList<>();

        // Takvim uyumu (Pazartesi=1, Pazar=7)
        int dayOfWeek = travelDate.getDayOfWeek().getValue();

        // 1. BAŞLANGIÇ: Başlangıç noktasından çıkan araçları bul, originId ile eşleşen ve
        List<Transportation> initialSteps = transportationRepository.findByOriginId(originId);
        for (Transportation t : initialSteps) {
            if (t.getOperatingDays().contains(dayOfWeek)) {
                List<Transportation> path = new ArrayList<>();
                path.add(t);
                queue.add(path);
            }
        }

        // 2. BFS DÖNGÜSÜ: Katman katman yolları genişlet
        while (!queue.isEmpty()) {
            List<Transportation> currentPath = queue.poll();
            Transportation lastStep = currentPath.get(currentPath.size() - 1);

            // Hedefe ulaşıldı mı?
            if (lastStep.getDestination().getId().equals(destinationId)) {
                if (validateRouteRules(currentPath)) {
                    allValidRoutes.add(new ArrayList<>(currentPath));
                }
                continue;
            }

            // maximum 2 aktarma (toplam 3)
            if (currentPath.size() >= 3) continue;

            // Komşu durakları gez
            List<Transportation> nextSteps = transportationRepository.findByOriginId(lastStep.getDestination().getId());
            for (Transportation next : nextSteps) {
                if (next.getOperatingDays().contains(dayOfWeek) && isNotCircular(currentPath, next)) {
                    List<Transportation> newPath = new ArrayList<>(currentPath);
                    newPath.add(next);
                    queue.add(newPath);
                }
            }
        }

        return allValidRoutes.stream()
                .map(path -> mapper.mapList(path, TransportationDTO.class))
                .collect(Collectors.toList());
    }
    // validateRouteRules ve isNotCircular metodları aynı kalır (Entity üzerinden kontrol eder)


    /**
     * Dokümandaki Valid/Invalid kurallarını denetler
     */
    private boolean validateRouteRules(List<Transportation> path) {
        // Kural: En az bir uçuş (FLIGHT) olmalı ve birden fazla olamaz
        List<Transportation> flights = path.stream()
                .filter(t -> t.getType().equalsIgnoreCase("FLIGHT"))
                .collect(Collectors.toList());

        if (flights.size() != 1) return false;

        int flightIndex = path.indexOf(flights.get(0));

        // Kural: Uçuştan önce birden fazla transfer olamaz (Örn: UBER -> BUS -> FLIGHT geçersiz)
        if (flightIndex > 1) return false;

        // Kural: Uçuştan sonra birden fazla transfer olamaz (Örn: FLIGHT -> SUBWAY -> UBER geçersiz)
        int stepsAfterFlight = path.size() - 1 - flightIndex;
        if (stepsAfterFlight > 1) return false;

        return true;
    }

    private boolean isNotCircular(List<Transportation> currentPath, Transportation next) {
        return currentPath.stream()
                .noneMatch(p -> p.getOrigin().getId().equals(next.getDestination().getId()));
    }
}