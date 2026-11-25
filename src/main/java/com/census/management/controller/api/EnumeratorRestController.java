package com.census.management.controller.api;

import com.census.management.dto.*;
import com.census.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/enumerators")
@PreAuthorize("hasRole('ADMIN')")
public class EnumeratorRestController {

    private final UserService userService;

    public EnumeratorRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getEnumerators(
            @RequestParam(name = "search", required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDto> page = userService.getEnumerators(pageable, search);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getEnumeratorById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getEnumeratorById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createEnumerator(@Valid @RequestBody CreateEnumeratorDto createDto) {
        UserDto created = userService.createEnumerator(createDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateEnumerator(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEnumeratorDto updateDto) {
        UserDto updated = userService.updateEnumerator(id, updateDto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EnumeratorStatusDto statusDto) {
        UserDto updated = userService.updateEnumeratorStatus(id, statusDto.getActive());
        return ResponseEntity.ok(updated);
    }
}
