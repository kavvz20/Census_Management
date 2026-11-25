package com.census.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PersonDto {

    private Long id;

    @NotBlank(message = "ID number is required")
    @Size(max = 20, message = "ID number must not exceed 20 characters")
    private String idNumber;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    @NotBlank(message = "Relationship to head of household is required")
    private String relation;

    @NotBlank(message = "Marital status is required")
    private String maritalStatus;

    @NotBlank(message = "Education level is required")
    private String education;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    private String nationality = "Indian";

    @NotNull(message = "Household is required")
    private Long householdId;

    private String householdAddress;
    private Long regionId;
    private String regionName;

    private int age;
    private boolean literate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PersonDto() {
        this.nationality = "Indian";
    }

    public PersonDto(Long id, String idNumber, String name, String gender, LocalDate dob, String relation,
                     String maritalStatus, String education, String occupation, String nationality,
                     Long householdId, String householdAddress, Long regionId, String regionName,
                     int age, boolean literate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.idNumber = idNumber;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.relation = relation;
        this.maritalStatus = maritalStatus;
        this.education = education;
        this.occupation = occupation;
        this.nationality = nationality;
        this.householdId = householdId;
        this.householdAddress = householdAddress;
        this.regionId = regionId;
        this.regionName = regionName;
        this.age = age;
        this.literate = literate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdAddress() {
        return householdAddress;
    }

    public void setHouseholdAddress(String householdAddress) {
        this.householdAddress = householdAddress;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isLiterate() {
        return literate;
    }

    public void setLiterate(boolean literate) {
        this.literate = literate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
