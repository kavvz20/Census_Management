package com.census.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "household")
public class Household extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 255)
    private String address;

    @NotNull(message = "Region is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @NotBlank(message = "House type is required")
    @Column(name = "house_type", nullable = false, length = 50)
    private String houseType;

    @Column(nullable = false)
    private boolean electricity;

    @Column(nullable = false, length = 50)
    private String water;

    @Size(max = 15, message = "Contact number must not exceed 15 digits")
    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Person> members = new ArrayList<>();

    public Household() {
    }

    public Household(String address, Region region, String houseType, boolean electricity, String water, String contactNumber) {
        this.address = address;
        this.region = region;
        this.houseType = houseType;
        this.electricity = electricity;
        this.water = water;
        this.contactNumber = contactNumber;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
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

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Household household)) return false;
        return Objects.equals(id, household.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
