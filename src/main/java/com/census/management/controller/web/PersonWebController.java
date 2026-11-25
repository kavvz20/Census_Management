package com.census.management.controller.web;

import com.census.management.dto.PersonDto;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.security.UserPrincipal;
import com.census.management.service.PersonService;
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

@Controller
@RequestMapping("/persons")
public class PersonWebController {

    private final PersonService personService;

    public PersonWebController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String listPersons(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "householdId", required = false) Long householdId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        Page<PersonDto> persons = personService.searchPersons(
                currentUser,
                name,
                householdId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        model.addAttribute("persons", persons);
        model.addAttribute("name", name);
        model.addAttribute("householdId", householdId);
        model.addAttribute("currentUser", currentUser);
        return "persons/list";
    }

    @PostMapping("/new")
    public String createPerson(
            @Valid @ModelAttribute("newMember") PersonDto personDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        Long householdId = personDto.getHouseholdId();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed. Please verify all required fields.");
            return "redirect:/households/" + householdId;
        }

        try {
            personService.createPerson(personDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Family member successfully added!");
        } catch (DuplicateResourceException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding member: " + ex.getMessage());
        }

        return "redirect:/households/" + householdId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            Model model) {

        PersonDto person = personService.getPersonById(id, currentUser);
        model.addAttribute("person", person);
        model.addAttribute("currentUser", currentUser);
        return "persons/form";
    }

    @PostMapping("/{id}/edit")
    public String updatePerson(
            @PathVariable Long id,
            @Valid @ModelAttribute("person") PersonDto personDto,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            return "persons/form";
        }

        try {
            personService.updatePerson(id, personDto, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Person record updated successfully!");
            return "redirect:/households/" + personDto.getHouseholdId();
        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("currentUser", currentUser);
            return "persons/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePerson(
            @PathVariable Long id,
            @RequestParam(name = "householdId", required = false) Long householdId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {

        personService.deletePerson(id, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Member record removed.");

        if (householdId != null) {
            return "redirect:/households/" + householdId;
        }
        return "redirect:/persons";
    }
}
