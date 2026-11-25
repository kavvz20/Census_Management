package com.census.management.service;

import com.census.management.dto.CreateEnumeratorDto;
import com.census.management.dto.UpdateEnumeratorDto;
import com.census.management.dto.UserDto;
import com.census.management.entity.Region;
import com.census.management.entity.Role;
import com.census.management.entity.User;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.exception.ResourceNotFoundException;
import com.census.management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RegionService regionService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RegionService regionService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.regionService = regionService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllEnumerators() {
        return userRepository.findByRole(Role.ENUMERATOR).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<UserDto> getEnumerators(Pageable pageable, String search) {
        Page<User> page;
        if (search != null && !search.trim().isEmpty()) {
            page = userRepository.findByRoleAndUsernameContainingIgnoreCase(Role.ENUMERATOR, search.trim(), pageable);
        } else {
            page = userRepository.findByRole(Role.ENUMERATOR, pageable);
        }
        return page.map(this::toDto);
    }

    public UserDto getEnumeratorById(Long id) {
        User user = userRepository.findByIdAndRole(id, Role.ENUMERATOR)
                .orElseThrow(() -> new ResourceNotFoundException("Enumerator", "id", id));
        return toDto(user);
    }

    public UserDto createEnumerator(CreateEnumeratorDto dto) {
        String username = dto.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already registered.");
        }

        Region region = regionService.getRegionEntity(dto.getRegionId());

        User enumerator = new User();
        enumerator.setUsername(username);
        enumerator.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        enumerator.setRole(Role.ENUMERATOR);
        enumerator.setRegion(region);
        enumerator.setActive(true);

        User saved = userRepository.save(enumerator);
        return toDto(saved);
    }

    public UserDto updateEnumerator(Long id, UpdateEnumeratorDto dto) {
        User user = userRepository.findByIdAndRole(id, Role.ENUMERATOR)
                .orElseThrow(() -> new ResourceNotFoundException("Enumerator", "id", id));

        String username = dto.getUsername().trim();
        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new DuplicateResourceException("Username '" + username + "' is already in use by another account.");
        }

        user.setUsername(username);

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword().trim()));
        }

        if (dto.getRegionId() != null) {
            Region region = regionService.getRegionEntity(dto.getRegionId());
            user.setRegion(region);
        }

        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    public UserDto updateEnumeratorStatus(Long id, boolean active) {
        User user = userRepository.findByIdAndRole(id, Role.ENUMERATOR)
                .orElseThrow(() -> new ResourceNotFoundException("Enumerator", "id", id));

        user.setActive(active);
        User updated = userRepository.save(user);
        return toDto(updated);
    }

    public UserDto toDto(User user) {
        Long regionId = user.getRegion() != null ? user.getRegion().getId() : null;
        String regionName = user.getRegion() != null ? user.getRegion().getFullName() : null;

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                regionId,
                regionName,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
