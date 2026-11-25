package com.census.management.service;

import com.census.management.dto.HouseholdDto;
import com.census.management.entity.Household;
import com.census.management.entity.Region;
import com.census.management.exception.RegionAccessDeniedException;
import com.census.management.exception.ResourceNotFoundException;
import com.census.management.repository.HouseholdRepository;
import com.census.management.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final RegionService regionService;

    public HouseholdService(HouseholdRepository householdRepository, RegionService regionService) {
        this.householdRepository = householdRepository;
        this.regionService = regionService;
    }

    public Page<HouseholdDto> getHouseholds(UserPrincipal currentUser, Long regionIdFilter, String keyword, Pageable pageable) {
        Long targetRegionId;

        if (currentUser.isEnumerator()) {
            // Enumerator is strictly locked to their assigned region! Request parameter is ignored.
            targetRegionId = currentUser.getRegionId();
        } else {
            // Admin can view all or filter by region
            targetRegionId = regionIdFilter;
        }

        Page<Household> page = householdRepository.searchHouseholds(targetRegionId, keyword, pageable);
        return page.map(this::toDto);
    }

    public HouseholdDto getHouseholdById(Long id, UserPrincipal currentUser) {
        Household household = getHouseholdEntity(id, currentUser);
        return toDto(household);
    }

    public Household getHouseholdEntity(Long id, UserPrincipal currentUser) {
        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Household", "id", id));

        // Region security check
        verifyRegionAccess(household.getRegion(), currentUser);

        return household;
    }

    public HouseholdDto createHousehold(HouseholdDto dto, UserPrincipal currentUser) {
        Region region;
        if (currentUser.isEnumerator()) {
            // Enforce the enumerator's assigned region
            region = regionService.getRegionEntity(currentUser.getRegionId());
        } else {
            // Admin can assign region from DTO
            region = regionService.getRegionEntity(dto.getRegionId());
        }

        Household household = new Household();
        household.setAddress(dto.getAddress().trim());
        household.setRegion(region);
        household.setHouseType(dto.getHouseType().trim());
        household.setElectricity(dto.isElectricity());
        household.setWater(dto.getWater().trim());
        household.setContactNumber(dto.getContactNumber() != null ? dto.getContactNumber().trim() : null);

        Household saved = householdRepository.save(household);
        return toDto(saved);
    }

    public HouseholdDto updateHousehold(Long id, HouseholdDto dto, UserPrincipal currentUser) {
        Household household = getHouseholdEntity(id, currentUser);

        household.setAddress(dto.getAddress().trim());
        household.setHouseType(dto.getHouseType().trim());
        household.setElectricity(dto.isElectricity());
        household.setWater(dto.getWater().trim());
        household.setContactNumber(dto.getContactNumber() != null ? dto.getContactNumber().trim() : null);

        if (currentUser.isAdmin() && dto.getRegionId() != null) {
            Region region = regionService.getRegionEntity(dto.getRegionId());
            household.setRegion(region);
        }

        Household updated = householdRepository.save(household);
        return toDto(updated);
    }

    public void deleteHousehold(Long id, UserPrincipal currentUser) {
        Household household = getHouseholdEntity(id, currentUser);
        householdRepository.delete(household);
    }

    public List<HouseholdDto> getRecentHouseholds(UserPrincipal currentUser) {
        List<Household> households;
        if (currentUser.isEnumerator()) {
            Region region = regionService.getRegionEntity(currentUser.getRegionId());
            households = householdRepository.findTop5ByRegionOrderByCreatedAtDesc(region);
        } else {
            households = householdRepository.findTop5ByOrderByCreatedAtDesc();
        }
        return households.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void verifyRegionAccess(Region region, UserPrincipal currentUser) {
        if (currentUser.isEnumerator()) {
            if (region == null || !Objects.equals(region.getId(), currentUser.getRegionId())) {
                throw new RegionAccessDeniedException(
                        "Access Denied: You do not have permission to view or modify records outside your assigned region ("
                                + currentUser.getRegionName() + ").");
            }
        }
    }

    public HouseholdDto toDto(Household household) {
        int membersCount = (household.getMembers() != null) ? household.getMembers().size() : 0;

        return new HouseholdDto(
                household.getId(),
                household.getAddress(),
                household.getRegion().getId(),
                household.getRegion().getFullName(),
                household.getHouseType(),
                household.isElectricity(),
                household.getWater(),
                household.getContactNumber(),
                membersCount,
                household.getCreatedAt(),
                household.getUpdatedAt()
        );
    }
}
