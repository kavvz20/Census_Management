package com.census.management.controller.api;

import com.census.management.dto.ApiResponse;
import com.census.management.dto.PersonDto;
import com.census.management.security.UserPrincipal;
import com.census.management.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonRestController {

    private final PersonService personService;

    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<Page<PersonDto>> getPersons(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "householdId", required = false) Long householdId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PersonDto> page = personService.searchPersons(currentUser, name, householdId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getPersonById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(personService.getPersonById(id, currentUser));
    }

    @GetMapping("/household/{householdId}")
    public ResponseEntity<List<PersonDto>> getMembersByHousehold(
            @PathVariable Long householdId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(personService.getMembersByHousehold(householdId, currentUser));
    }

    @PostMapping
    public ResponseEntity<PersonDto> createPerson(
            @Valid @RequestBody PersonDto personDto,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PersonDto created = personService.createPerson(personDto, currentUser);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonDto personDto,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PersonDto updated = personService.updatePerson(id, personDto, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePerson(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        personService.deletePerson(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Person record successfully deleted."));
    }
}
