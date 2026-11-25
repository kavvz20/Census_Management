package com.census.management;

import com.census.management.dto.HouseholdDto;
import com.census.management.dto.PersonDto;
import com.census.management.dto.RegionDto;
import com.census.management.dto.SummaryReportDto;
import com.census.management.entity.Household;
import com.census.management.entity.Role;
import com.census.management.entity.User;
import com.census.management.exception.DuplicateResourceException;
import com.census.management.exception.RegionAccessDeniedException;
import com.census.management.repository.HouseholdRepository;
import com.census.management.security.UserPrincipal;
import com.census.management.service.HouseholdService;
import com.census.management.service.PersonService;
import com.census.management.service.RegionService;
import com.census.management.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CensusManagementApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegionService regionService;

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("Context loads and seed data is populated")
    void contextLoads() {
        assertNotNull(regionService);
        assertNotNull(householdService);
        assertNotNull(personService);
        assertNotNull(reportService);

        SummaryReportDto summary = reportService.getSummaryReport(null);
        assertTrue(summary.getTotalPopulation() >= 15, "Should have seeded population");
        assertTrue(summary.getTotalHouseholds() >= 5, "Should have seeded households");
    }

    @Test
    @DisplayName("Duplicate Region constraint test")
    void testDuplicateRegion() {
        RegionDto dto = new RegionDto();
        dto.setState("Maharashtra");
        dto.setDistrict("Mumbai Suburban");
        dto.setCity("Andheri");

        assertThrows(DuplicateResourceException.class, () -> {
            regionService.createRegion(dto);
        }, "Duplicate State + District + City must throw DuplicateResourceException");
    }

    @Test
    @DisplayName("Duplicate Person National ID constraint test")
    void testDuplicatePersonIdNumber() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);
        UserPrincipal adminPrincipal = UserPrincipal.create(adminUser);

        Household firstHousehold = householdRepository.findAll().get(0);

        PersonDto p = new PersonDto();
        p.setIdNumber("MH-1001-A"); // already exists in seed data
        p.setName("Duplicate Tester");
        p.setGender("Male");
        p.setDob(LocalDate.of(1990, 1, 1));
        p.setRelation("Head");
        p.setMaritalStatus("Single");
        p.setEducation("Graduate");
        p.setOccupation("Employed");
        p.setHouseholdId(firstHousehold.getId());

        assertThrows(DuplicateResourceException.class, () -> {
            personService.createPerson(p, adminPrincipal);
        }, "Duplicate national ID number must throw DuplicateResourceException");
    }

    @Test
    @DisplayName("Enumerator Region Boundary Enforcement: Accessing other region household throws RegionAccessDeniedException")
    void testEnumeratorRegionSecurity() {
        Household otherRegionHousehold = householdRepository.findAll().stream()
                .filter(h -> !h.getRegion().getId().equals(1L))
                .findFirst()
                .orElseThrow();

        UserPrincipal mumbaiEnum = new UserPrincipal(
                2L, "enum_mumbai", "pass", Role.ENUMERATOR, 1L, "Andheri, Mumbai Suburban (Maharashtra)", true,
                Collections.emptyList());

        assertThrows(RegionAccessDeniedException.class, () -> {
            householdService.getHouseholdById(otherRegionHousehold.getId(), mumbaiEnum);
        }, "Enumerator must not be allowed to access households outside their assigned region");
    }

    @Test
    @DisplayName("Thymeleaf View Rendering: /login returns HTTP 200 with HTML title")
    void testLoginViewRenders() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(xpath("//title").string("Login - Census Management System"));
    }

    @Test
    @WithUserDetails("enum_mumbai")
    @DisplayName("Thymeleaf View Rendering: /enumerator-home renders successfully with assigned region title and content")
    void testEnumeratorHomeViewRenders() throws Exception {
        mockMvc.perform(get("/enumerator-home"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(xpath("//title").string("Enumerator Portal - Census Management System"));
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("Thymeleaf View Rendering: /dashboard renders successfully for Admin")
    void testAdminDashboardViewRenders() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(xpath("//title").string("Admin Dashboard - Census Management System"));
    }
}
