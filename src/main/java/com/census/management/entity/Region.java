package com.census.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(
    name = "region",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_region_state_district_city", columnNames = {"state", "district", "city"})
    }
)
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "State is required")
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank(message = "District is required")
    @Column(nullable = false, length = 100)
    private String district;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 100)
    private String city;

    public Region() {
    }

    public Region(String state, String district, String city) {
        this.state = state;
        this.district = district;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Transient
    public String getFullName() {
        return city + ", " + district + " (" + state + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;
        return Objects.equals(id, region.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
