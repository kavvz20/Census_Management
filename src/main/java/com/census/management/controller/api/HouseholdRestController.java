package com.census.management.controller.api;

import com.census.management.dto.ApiResponse;
import com.census.management.dto.HouseholdDto;
import com.census.management.security.UserPrincipal;
import com.census.management.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/households")
public class HouseholdRestController {

    private final HouseholdService householdService;

    public HouseholdRestController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @GetMapping
    public ResponseEntity<Page<HouseholdDto>> getHouseholds(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "regionId", required = false) Long regionId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<HouseholdDto> page = householdService.getHouseholds(currentUser, regionId, keyword, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HouseholdDto> getHouseholdById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(householdService.getHouseholdById(id, currentUser));
    }

    @PostMapping
    public ResponseEntity<HouseholdDto> createHousehold(
            @Valid @RequestBody HouseholdDto householdDto,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        HouseholdDto created = householdService.createHousehold(householdDto, currentUser);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseholdDto> updateHousehold(
            @PathVariable Long id,
            @Valid @RequestBody HouseholdDto householdDto,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        HouseholdDto updated = householdService.updateHousehold(id, householdDto, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHousehold(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        householdService.deleteHousehold(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Household successfully deleted."));
    }
}
