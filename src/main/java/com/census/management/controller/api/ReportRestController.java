package com.census.management.controller.api;

import com.census.management.dto.AgeGroupReportDto;
import com.census.management.dto.SummaryReportDto;
import com.census.management.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportRestController {

    private final ReportService reportService;

    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryReportDto> getSummaryReport(
            @RequestParam(name = "regionId", required = false) Long regionId) {
        return ResponseEntity.ok(reportService.getSummaryReport(regionId));
    }

    @GetMapping("/age-groups")
    public ResponseEntity<AgeGroupReportDto> getAgeGroupReport(
            @RequestParam(name = "regionId", required = false) Long regionId) {
        return ResponseEntity.ok(reportService.getAgeGroupReport(regionId));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(name = "regionId", required = false) Long regionId) {
        String csvData = reportService.exportToCsv(regionId);
        byte[] bytes = csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        String filename = "census_report_" + (regionId != null ? "region_" + regionId : "national") + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
