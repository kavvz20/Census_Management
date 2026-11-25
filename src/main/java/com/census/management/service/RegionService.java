package com.census.management.service;

import com.census.management.dto.RegionDto;
import com.census.management.entity.Region;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.exception.ResourceNotFoundException;
import com.census.management.repository.RegionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<RegionDto> getAllRegions() {
        return regionRepository.findAllByOrderByStateAscDistrictAscCityAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<RegionDto> getRegions(Pageable pageable, String search) {
        Page<Region> page;
        if (search != null && !search.trim().isEmpty()) {
            page = regionRepository.findByStateContainingIgnoreCaseOrDistrictContainingIgnoreCaseOrCityContainingIgnoreCase(
                    search.trim(), search.trim(), search.trim(), pageable);
        } else {
            page = regionRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    public RegionDto getRegionById(Long id) {
        Region region = getRegionEntity(id);
        return toDto(region);
    }

    public Region getRegionEntity(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
    }

    public RegionDto createRegion(RegionDto dto) {
        String state = dto.getState().trim();
        String district = dto.getDistrict().trim();
        String city = dto.getCity().trim();

        if (regionRepository.existsByStateIgnoreCaseAndDistrictIgnoreCaseAndCityIgnoreCase(state, district, city)) {
            throw new DuplicateResourceException(
                    String.format("Region '%s, %s (%s)' already exists.", city, district, state));
        }

        Region region = new Region(state, district, city);
        Region saved = regionRepository.save(region);
        return toDto(saved);
    }

    public RegionDto updateRegion(Long id, RegionDto dto) {
        Region region = getRegionEntity(id);

        String state = dto.getState().trim();
        String district = dto.getDistrict().trim();
        String city = dto.getCity().trim();

        if (regionRepository.existsByStateIgnoreCaseAndDistrictIgnoreCaseAndCityIgnoreCaseAndIdNot(state, district, city, id)) {
            throw new DuplicateResourceException(
                    String.format("Region '%s, %s (%s)' already exists.", city, district, state));
        }

        region.setState(state);
        region.setDistrict(district);
        region.setCity(city);

        Region updated = regionRepository.save(region);
        return toDto(updated);
    }

    public void deleteRegion(Long id) {
        Region region = getRegionEntity(id);
        regionRepository.delete(region);
    }

    public RegionDto toDto(Region region) {
        return new RegionDto(
                region.getId(),
                region.getState(),
                region.getDistrict(),
                region.getCity(),
                region.getFullName(),
                region.getCreatedAt(),
                region.getUpdatedAt()
        );
    }
}
