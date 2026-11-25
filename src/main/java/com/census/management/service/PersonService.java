package com.census.management.service;

import com.census.management.dto.PersonDto;
import com.census.management.entity.Household;
import com.census.management.entity.Person;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.exception.RegionAccessDeniedException;
import com.census.management.exception.ResourceNotFoundException;
import com.census.management.repository.PersonRepository;
import com.census.management.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final HouseholdService householdService;

    public PersonService(PersonRepository personRepository, HouseholdService householdService) {
        this.personRepository = personRepository;
        this.householdService = householdService;
    }

    public Page<PersonDto> searchPersons(UserPrincipal currentUser, String name, Long householdId, Pageable pageable) {
        Long targetRegionId = null;

        if (currentUser.isEnumerator()) {
            targetRegionId = currentUser.getRegionId();
        }

        // If a specific householdId is provided, verify region permission
        if (householdId != null) {
            householdService.getHouseholdEntity(householdId, currentUser);
        }

        Page<Person> page = personRepository.searchPersons(targetRegionId, householdId, name, pageable);
        return page.map(this::toDto);
    }

    public List<PersonDto> getMembersByHousehold(Long householdId, UserPrincipal currentUser) {
        // This will check region security
        Household household = householdService.getHouseholdEntity(householdId, currentUser);

        return personRepository.findByHouseholdId(household.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PersonDto getPersonById(Long id, UserPrincipal currentUser) {
        Person person = getPersonEntity(id, currentUser);
        return toDto(person);
    }

    public Person getPersonEntity(Long id, UserPrincipal currentUser) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));

        // Region security check through household (person -> household -> region)
        verifyPersonRegionAccess(person, currentUser);

        return person;
    }

    public PersonDto createPerson(PersonDto dto, UserPrincipal currentUser) {
        // Check uniqueness of ID number
        String idNumber = dto.getIdNumber().trim();
        if (personRepository.existsByIdNumber(idNumber)) {
            throw new DuplicateResourceException(
                    "Person with National ID Number '" + idNumber + "' already exists in the census database.");
        }

        // Verify household exists and user has access to that household's region
        Household household = householdService.getHouseholdEntity(dto.getHouseholdId(), currentUser);

        Person person = new Person();
        person.setIdNumber(idNumber);
        person.setName(dto.getName().trim());
        person.setGender(dto.getGender().trim());
        person.setDob(dto.getDob());
        person.setRelation(dto.getRelation().trim());
        person.setMaritalStatus(dto.getMaritalStatus().trim());
        person.setEducation(dto.getEducation().trim());
        person.setOccupation(dto.getOccupation().trim());
        person.setNationality(dto.getNationality() != null && !dto.getNationality().trim().isEmpty() ?
                dto.getNationality().trim() : "Indian");
        person.setHousehold(household);

        Person saved = personRepository.save(person);
        return toDto(saved);
    }

    public PersonDto updatePerson(Long id, PersonDto dto, UserPrincipal currentUser) {
        Person person = getPersonEntity(id, currentUser);

        String idNumber = dto.getIdNumber().trim();
        if (personRepository.existsByIdNumberAndIdNot(idNumber, id)) {
            throw new DuplicateResourceException(
                    "Person with National ID Number '" + idNumber + "' is already registered to another individual.");
        }

        if (dto.getHouseholdId() != null && !Objects.equals(person.getHousehold().getId(), dto.getHouseholdId())) {
            Household newHousehold = householdService.getHouseholdEntity(dto.getHouseholdId(), currentUser);
            person.setHousehold(newHousehold);
        }

        person.setIdNumber(idNumber);
        person.setName(dto.getName().trim());
        person.setGender(dto.getGender().trim());
        person.setDob(dto.getDob());
        person.setRelation(dto.getRelation().trim());
        person.setMaritalStatus(dto.getMaritalStatus().trim());
        person.setEducation(dto.getEducation().trim());
        person.setOccupation(dto.getOccupation().trim());
        if (dto.getNationality() != null && !dto.getNationality().trim().isEmpty()) {
            person.setNationality(dto.getNationality().trim());
        }

        Person updated = personRepository.save(person);
        return toDto(updated);
    }

    public void deletePerson(Long id, UserPrincipal currentUser) {
        Person person = getPersonEntity(id, currentUser);
        personRepository.delete(person);
    }

    private void verifyPersonRegionAccess(Person person, UserPrincipal currentUser) {
        if (currentUser.isEnumerator()) {
            Long personRegionId = person.getHousehold().getRegion().getId();
            if (!Objects.equals(personRegionId, currentUser.getRegionId())) {
                throw new RegionAccessDeniedException(
                        "Access Denied: You do not have permission to view or modify citizen records outside your assigned region ("
                                + currentUser.getRegionName() + ").");
            }
        }
    }

    public PersonDto toDto(Person person) {
        Household h = person.getHousehold();
        Long regionId = h.getRegion() != null ? h.getRegion().getId() : null;
        String regionName = h.getRegion() != null ? h.getRegion().getFullName() : null;

        return new PersonDto(
                person.getId(),
                person.getIdNumber(),
                person.getName(),
                person.getGender(),
                person.getDob(),
                person.getRelation(),
                person.getMaritalStatus(),
                person.getEducation(),
                person.getOccupation(),
                person.getNationality(),
                h.getId(),
                h.getAddress(),
                regionId,
                regionName,
                person.getAge(),
                person.isLiterate(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}
