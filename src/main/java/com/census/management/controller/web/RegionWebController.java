package com.census.management.controller.web;

import com.census.management.dto.RegionDto;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.security.UserPrincipal;
import com.census.management.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/regions")
@PreAuthorize("hasRole('ADMIN')")
public class RegionWebController {

    private final RegionService regionService;

    public RegionWebController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public String listRegions(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        Page<RegionDto> regions = regionService.getRegions(
                PageRequest.of(page, size, Sort.by("state").ascending().and(Sort.by("district").ascending())),
                search
        );

        model.addAttribute("regions", regions);
        model.addAttribute("search", search);
        model.addAttribute("currentUser", currentUser);
        return "regions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        model.addAttribute("region", new RegionDto());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentUser", currentUser);
        return "regions/form";
    }

    @PostMapping("/new")
    public String createRegion(
            @Valid @ModelAttribute("region") RegionDto regionDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("currentUser", currentUser);
            return "regions/form";
        }

        try {
            regionService.createRegion(regionDto);
            redirectAttributes.addFlashAttribute("successMessage", "Region created successfully!");
            return "redirect:/regions";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("currentUser", currentUser);
            return "regions/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        RegionDto region = regionService.getRegionById(id);
        model.addAttribute("region", region);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentUser", currentUser);
        return "regions/form";
    }

    @PostMapping("/edit/{id}")
    public String updateRegion(
            @PathVariable Long id,
            @Valid @ModelAttribute("region") RegionDto regionDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("currentUser", currentUser);
            return "regions/form";
        }

        try {
            regionService.updateRegion(id, regionDto);
            redirectAttributes.addFlashAttribute("successMessage", "Region updated successfully!");
            return "redirect:/regions";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("currentUser", currentUser);
            return "regions/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteRegion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            regionService.deleteRegion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Region deleted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete region: " + ex.getMessage());
        }
        return "redirect:/regions";
    }
}
