package com.example.thy.service;

import com.example.thy.dto.TransportationDTO;
import com.example.thy.dto.TransportationSaveDTO;
import com.example.thy.mapper.ApplicationMapper;
import com.example.thy.model.Location;
import com.example.thy.model.Transportation;
import com.example.thy.repository.LocationRepository;
import com.example.thy.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final ApplicationMapper mapper;

    public List<Transportation> findAll() {
        return transportationRepository.findAll();
    }

    @CacheEvict(value = "routes", allEntries = true)
    @Transactional
    public TransportationDTO save(TransportationSaveDTO saveDTO) {
        // 1. Lokasyonları bul
        Location origin = locationRepository.findByLocationCode(saveDTO.getOriginCode())
                .orElseThrow(() -> new RuntimeException("Kalkış lokasyonu bulunamadı: " + saveDTO.getOriginCode()));

        Location destination = locationRepository.findByLocationCode(saveDTO.getDestinationCode())
                .orElseThrow(() -> new RuntimeException("Varış lokasyonu bulunamadı: " + saveDTO.getDestinationCode()));

        //  VALIDATION KATMANI
        validateTransportation(saveDTO, origin, destination);


        // 2. Nesne oluştur ve kaydet
        Transportation transportation = new Transportation();
        transportation.setOrigin(origin);
        transportation.setDestination(destination);
        transportation.setType(saveDTO.getType());
        transportation.setOperatingDays(saveDTO.getOperatingDays());

        Transportation saved = transportationRepository.save(transportation);
        return convertToDTO(saved);
    }

    /**
     * İş Kuralları Validasyonu (Business Rules Validation)
     */
    private void validateTransportation(TransportationSaveDTO dto, Location origin, Location destination) {
        // 1. Temel Kontrol: Aynı yer olamaz
        if (origin.getId().equals(destination.getId())) {
            throw new RuntimeException("Kalkış ve varış noktası aynı olamaz.");
        }

        // 2. Gün Kontrolü: En az bir gün seçilmeli
        if (dto.getOperatingDays() == null || dto.getOperatingDays().isEmpty()) {
            throw new RuntimeException("Lütfen ulaşımın aktif olduğu en az bir gün seçiniz.");
        }

        String type = dto.getType().toUpperCase();
        int originLen = origin.getLocationCode().length();
        int destLen = destination.getLocationCode().length();

        // 3. IATA Uçuş Kontrolü (3 Hane Kuralı)
        if ("FLIGHT".equals(type)) {
            if (originLen != 3 || destLen != 3) {
                throw new RuntimeException("Uçuşlar sadece 3 haneli IATA koduna sahip havaalanları arasında tanımlanabilir.");
            }
        }
        if ("FLIGHT".equals(type)) {
            if (originLen != 3 || destLen != 3) {
                throw new RuntimeException("Uçuşlar sadece 3 haneli IATA koduna sahip havaalanları arasında tanımlanabilir.");
            }
        }

        if (!"FLIGHT".equals(type)) {
            if (originLen == 3 && destLen == 3) {
                throw new RuntimeException("İki IATA kodlu nokta (havaalanı) arasında sadece 'Uçuş' (Flight) tanımlanabilir.");
            }
        }

    }

    @CacheEvict(value = "routes", allEntries = true)
    public void delete(Long id) {
        transportationRepository.deleteById(id);
    }

    public List<TransportationDTO> getTransportationsByOrigin(Long originId) {
        List<Transportation> entities = transportationRepository.findByOriginId(originId);
        return mapper.mapList(entities, TransportationDTO.class);
    }

    public TransportationDTO convertToDTO(Transportation t) {
        TransportationDTO dto = new TransportationDTO();
        dto.setId(t.getId());
        dto.setType(t.getType());
        dto.setOperatingDays(t.getOperatingDays());

        if (t.getOrigin() != null) {
            dto.setOriginName(t.getOrigin().getName());
            dto.setOriginCode(t.getOrigin().getLocationCode());
        }

        if (t.getDestination() != null) {
            dto.setDestinationName(t.getDestination().getName());
            dto.setDestinationCode(t.getDestination().getLocationCode());
        }

        return dto;
    }

    public List<TransportationDTO> findAllAsDTO() {
        return findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "routes", allEntries = true)
    @Transactional
    public TransportationDTO update(Long id, TransportationSaveDTO updateDTO) {
        Transportation transportation = transportationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek kayıt bulunamadı: " + id));

        Location origin = locationRepository.findByLocationCode(updateDTO.getOriginCode())
                .orElseThrow(() -> new RuntimeException("Kalkış lokasyonu bulunamadı: " + updateDTO.getOriginCode()));

        Location destination = locationRepository.findByLocationCode(updateDTO.getDestinationCode())
                .orElseThrow(() -> new RuntimeException("Varış lokasyonu bulunamadı: " + updateDTO.getDestinationCode()));

        validateTransportation(updateDTO, origin, destination);

        transportation.setOrigin(origin);
        transportation.setDestination(destination);
        transportation.setType(updateDTO.getType());
        transportation.setOperatingDays(updateDTO.getOperatingDays());

        Transportation updated = transportationRepository.save(transportation);
        return mapper.map(updated, TransportationDTO.class);
    }
}