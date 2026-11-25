package com.census.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class HouseholdDto {

    private Long id;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Region is required")
    private Long regionId;

    private String regionName;

    @NotBlank(message = "House type is required")
    private String houseType;

    private boolean electricity;

    @NotBlank(message = "Water supply type is required")
    private String water;

    @Size(max = 15, message = "Contact number must not exceed 15 digits")
    private String contactNumber;

    private int memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HouseholdDto() {
    }

    public HouseholdDto(Long id, String address, Long regionId, String regionName, String houseType,
                        boolean electricity, String water, String contactNumber, int memberCount,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.address = address;
        this.regionId = regionId;
        this.regionName = regionName;
        this.houseType = houseType;
        this.electricity = electricity;
        this.water = water;
        this.contactNumber = contactNumber;
        this.memberCount = memberCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public boolean isElectricity() {
        return electricity;
    }

    public void setElectricity(boolean electricity) {
        this.electricity = electricity;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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
