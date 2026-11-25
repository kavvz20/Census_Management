package com.census.management.controller.api;

import com.census.management.dto.ApiResponse;
import com.census.management.dto.RegionDto;
import com.census.management.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@PreAuthorize("hasRole('ADMIN')")
public class RegionRestController {

    private final RegionService regionService;

    public RegionRestController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ResponseEntity<?> getRegions(
            @RequestParam(name = "all", defaultValue = "false") boolean all,
            @RequestParam(name = "search", required = false) String search,
            @PageableDefault(size = 10, sort = "state", direction = Sort.Direction.ASC) Pageable pageable) {

        if (all) {
            List<RegionDto> list = regionService.getAllRegions();
            return ResponseEntity.ok(list);
        }
        Page<RegionDto> page = regionService.getRegions(pageable, search);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionDto> getRegionById(@PathVariable Long id) {
        return ResponseEntity.ok(regionService.getRegionById(id));
    }

    @PostMapping
    public ResponseEntity<RegionDto> createRegion(@Valid @RequestBody RegionDto regionDto) {
        RegionDto created = regionService.createRegion(regionDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionDto> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionDto regionDto) {
        RegionDto updated = regionService.updateRegion(id, regionDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.ok(ApiResponse.success("Region successfully deleted."));
    }
}
