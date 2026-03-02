package com.example.thy.service;

import com.example.thy.dto.TransportationDTO;
import com.example.thy.mapper.ApplicationMapper;
import com.example.thy.model.Location;
import com.example.thy.model.Transportation;
import com.example.thy.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock private TransportationRepository transportationRepository;
    @Mock private ApplicationMapper mapper;
    @InjectMocks private RouteService routeService;

    private Location ist, lhr, cdg, saw, esb, ayt;
    private final LocalDate travelDate = LocalDate.of(2026, 3, 2); // Pazartesi (1)
    private final List<Integer> monday = List.of(1);

    @BeforeEach
    void init() {
        ist = TestDataFactory.createLocation(1L, "Istanbul", "IST");
        lhr = TestDataFactory.createLocation(2L, "London", "LHR");
        cdg = TestDataFactory.createLocation(3L, "Paris", "CDG");
        saw = TestDataFactory.createLocation(4L, "Sabiha", "SAW");
        esb = TestDataFactory.createLocation(5L, "Ankara", "ESB");
        ayt = TestDataFactory.createLocation(6L, "Antalya", "AYT");
    }

    private void mockTransport(Location origin, Transportation... transports) {
        when(transportationRepository.findByOriginId(origin.getId()))
                .thenReturn(Arrays.asList(transports));
    }

    @Test
    @DisplayName("Gereksiz aktarmalı (Uçuştan önce > 1) rotalar reddedilmelidir")
    void shouldRejectInvalidTransferRoutes() {
        // GIVEN: IST -(Uber)-> SAW -(Bus)-> ESB -(Flight)-> AYT
        Transportation uber = TestDataFactory.createTransport(ist, saw, "UBER", monday);
        Transportation bus = TestDataFactory.createTransport(saw, esb, "BUS", monday);
        Transportation flight = TestDataFactory.createTransport(esb, ayt, "FLIGHT", monday);

        mockTransport(ist, uber);
        mockTransport(saw, bus);
        mockTransport(esb, flight);

        // WHEN
        var result = routeService.findRoutes(ist.getId(), ayt.getId(), travelDate);

        // THEN
        assertTrue(result.isEmpty(), "Fazla transferli rota elenmeli!");
    }

    @Test
    @DisplayName("Geçerli aktarmalı (Bus -> Flight) rotalar bulunmalıdır")
    void shouldFindValidTransferRoutes() {
        Transportation bus = TestDataFactory.createTransport(ist, cdg, "BUS", monday);
        Transportation flight = TestDataFactory.createTransport(cdg, lhr, "FLIGHT", monday);

        mockTransport(ist, bus);
        mockTransport(cdg, flight);

        when(mapper.mapList(anyList(), eq(TransportationDTO.class))).thenReturn(List.of(new TransportationDTO()));

        var result = routeService.findRoutes(ist.getId(), lhr.getId(), travelDate);

        assertEquals(1, result.size());
    }
}