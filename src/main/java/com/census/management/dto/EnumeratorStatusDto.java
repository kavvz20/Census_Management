package com.census.management.dto;

import jakarta.validation.constraints.NotNull;

public class EnumeratorStatusDto {

    @NotNull(message = "Active status is required")
    private Boolean active;

    public EnumeratorStatusDto() {
    }

    public EnumeratorStatusDto(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
