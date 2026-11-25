package com.census.management.controller.web;

import com.census.management.dto.AgeGroupReportDto;
import com.census.management.dto.SummaryReportDto;
import com.census.management.security.UserPrincipal;
import com.census.management.service.RegionService;
import com.census.management.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportWebController {

    private final ReportService reportService;
    private final RegionService regionService;

    public ReportWebController(ReportService reportService, RegionService regionService) {
        this.reportService = reportService;
        this.regionService = regionService;
    }

    @GetMapping
    public String viewReports(
            @RequestParam(name = "regionId", required = false) Long regionId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        SummaryReportDto summary = reportService.getSummaryReport(regionId);
        AgeGroupReportDto ageGroups = reportService.getAgeGroupReport(regionId);

        model.addAttribute("summary", summary);
        model.addAttribute("ageGroups", ageGroups);
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("selectedRegionId", regionId);
        model.addAttribute("currentUser", currentUser);

        return "reports/dashboard";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(name = "regionId", required = false) Long regionId) {

        String csvData = reportService.exportToCsv(regionId);
        byte[] bytes = csvData.getBytes(StandardCharsets.UTF_8);

        String filename = "census_report_" + (regionId != null ? "region_" + regionId : "national") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
