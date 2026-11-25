package com.census.management.controller.web;

import com.census.management.dto.CreateEnumeratorDto;
import com.census.management.dto.UpdateEnumeratorDto;
import com.census.management.dto.UserDto;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.security.UserPrincipal;
import com.census.management.service.RegionService;
import com.census.management.service.UserService;
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
@RequestMapping("/enumerators")
@PreAuthorize("hasRole('ADMIN')")
public class EnumeratorWebController {

    private final UserService userService;
    private final RegionService regionService;

    public EnumeratorWebController(UserService userService, RegionService regionService) {
        this.userService = userService;
        this.regionService = regionService;
    }

    @GetMapping
    public String listEnumerators(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        Page<UserDto> enumerators = userService.getEnumerators(
                PageRequest.of(page, size, Sort.by("createdAt").descending()),
                search
        );

        model.addAttribute("enumerators", enumerators);
        model.addAttribute("search", search);
        model.addAttribute("currentUser", currentUser);
        return "enumerators/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        model.addAttribute("enumerator", new CreateEnumeratorDto());
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currentUser", currentUser);
        return "enumerators/form";
    }

    @PostMapping("/new")
    public String createEnumerator(
            @Valid @ModelAttribute("enumerator") CreateEnumeratorDto createDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("currentUser", currentUser);
            return "enumerators/form";
        }

        try {
            userService.createEnumerator(createDto);
            redirectAttributes.addFlashAttribute("successMessage", "Enumerator account created successfully!");
            return "redirect:/enumerators";
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("currentUser", currentUser);
            return "enumerators/form";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable Long id,
            @RequestParam(name = "active") boolean active,
            RedirectAttributes redirectAttributes) {

        userService.updateEnumeratorStatus(id, active);
        String msg = active ? "Enumerator account activated." : "Enumerator account deactivated.";
        redirectAttributes.addFlashAttribute("successMessage", msg);
        return "redirect:/enumerators";
    }
}
