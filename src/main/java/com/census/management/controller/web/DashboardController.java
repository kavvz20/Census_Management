package com.census.management.controller.web;

import com.census.management.dto.AgeGroupReportDto;
import com.census.management.dto.HouseholdDto;
import com.census.management.dto.SummaryReportDto;
import com.census.management.security.UserPrincipal;
import com.census.management.service.HouseholdService;
import com.census.management.service.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ReportService reportService;
    private final HouseholdService householdService;

    public DashboardController(ReportService reportService, HouseholdService householdService) {
        this.reportService = reportService;
        this.householdService = householdService;
    }

    @GetMapping("/")
    public String rootRedirect(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.isEnumerator()) {
            return "redirect:/enumerator-home";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        if (currentUser.isEnumerator()) {
            return "redirect:/enumerator-home";
        }

        SummaryReportDto summary = reportService.getSummaryReport(null);
        AgeGroupReportDto ageGroups = reportService.getAgeGroupReport(null);
        List<HouseholdDto> recentHouseholds = householdService.getRecentHouseholds(currentUser);

        model.addAttribute("summary", summary);
        model.addAttribute("ageGroups", ageGroups);
        model.addAttribute("recentHouseholds", recentHouseholds);
        model.addAttribute("currentUser", currentUser);

        return "dashboard";
    }

    @GetMapping("/enumerator-home")
    public String enumeratorHome(@AuthenticationPrincipal UserPrincipal currentUser, Model model) {
        if (currentUser.isAdmin()) {
            return "redirect:/dashboard";
        }

        Long regionId = currentUser.getRegionId();
        SummaryReportDto summary = reportService.getSummaryReport(regionId);
        List<HouseholdDto> recentHouseholds = householdService.getRecentHouseholds(currentUser);

        model.addAttribute("summary", summary);
        model.addAttribute("recentHouseholds", recentHouseholds);
        model.addAttribute("currentUser", currentUser);

        return "enumerator-home";
    }
}
