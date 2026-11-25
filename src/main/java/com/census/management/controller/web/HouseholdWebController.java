package com.census.management.controller.web;

import com.census.management.dto.HouseholdDto;
import com.census.management.dto.PersonDto;
import com.census.management.security.UserPrincipal;
import com.census.management.service.HouseholdService;
import com.census.management.service.PersonService;
import com.census.management.service.RegionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/households")
public class HouseholdWebController {

    private final HouseholdService householdService;
    private final PersonService personService;
    private final RegionService regionService;

    public HouseholdWebController(HouseholdService householdService,
                                  PersonService personService,
                                  RegionService regionService) {
        this.householdService = householdService;
        this.personService = personService;
        this.regionService = regionService;
    }

    @GetMapping
    public String listHouseholds(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "regionId", required = false) Long regionId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        Page<HouseholdDto> households = householdService.getHouseholds(
                currentUser,
                regionId,
                keyword,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        model.addAttribute("households", households);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRegionId", regionId);
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currentUser", currentUser);
        return "households/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        HouseholdDto dto = new HouseholdDto();
        if (currentUser.isEnumerator()) {
            dto.setRegionId(currentUser.getRegionId());
        }

        model.addAttribute("household", dto);
        model.addAttribute("isEdit", false);
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currentUser", currentUser);
        return "households/form";
    }

    @PostMapping("/new")
    public String createHousehold(
            @Valid @ModelAttribute("household") HouseholdDto householdDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("currentUser", currentUser);
            return "households/form";
        }

        HouseholdDto created = householdService.createHousehold(householdDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Household registered successfully!");
        return "redirect:/households/" + created.getId();
    }

    @GetMapping("/{id}")
    public String viewHousehold(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        HouseholdDto household = householdService.getHouseholdById(id, currentUser);
        List<PersonDto> members = personService.getMembersByHousehold(id, currentUser);

        PersonDto newMember = new PersonDto();
        newMember.setHouseholdId(id);

        model.addAttribute("household", household);
        model.addAttribute("members", members);
        model.addAttribute("newMember", newMember);
        model.addAttribute("currentUser", currentUser);
        return "households/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        HouseholdDto household = householdService.getHouseholdById(id, currentUser);
        model.addAttribute("household", household);
        model.addAttribute("isEdit", true);
        model.addAttribute("regions", regionService.getAllRegions());
        model.addAttribute("currentUser", currentUser);
        return "households/form";
    }

    @PostMapping("/{id}/edit")
    public String updateHousehold(
            @PathVariable Long id,
            @Valid @ModelAttribute("household") HouseholdDto householdDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("regions", regionService.getAllRegions());
            model.addAttribute("currentUser", currentUser);
            return "households/form";
        }

        householdService.updateHousehold(id, householdDto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Household updated successfully!");
        return "redirect:/households/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteHousehold(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        householdService.deleteHousehold(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Household record deleted.");
        return "redirect:/households";
    }
}
