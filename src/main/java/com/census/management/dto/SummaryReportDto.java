package com.census.management.dto;

import java.util.Map;

public class SummaryReportDto {

    private Long regionId;
    private String regionName;
    private long totalPopulation;
    private long totalHouseholds;
    private long maleCount;
    private long femaleCount;
    private long otherGenderCount;
    private double genderRatio; // Females per 1000 males
    private long literateCount;
    private long illiterateCount;
    private double literacyRate; // Percentage
    private Map<String, Long> occupationBreakdown;
    private Map<String, Long> nationalityBreakdown;

    public SummaryReportDto() {
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

    public long getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(long totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public long getTotalHouseholds() {
        return totalHouseholds;
    }

    public void setTotalHouseholds(long totalHouseholds) {
        this.totalHouseholds = totalHouseholds;
    }

    public long getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(long maleCount) {
        this.maleCount = maleCount;
    }

    public long getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(long femaleCount) {
        this.femaleCount = femaleCount;
    }

    public long getOtherGenderCount() {
        return otherGenderCount;
    }

    public void setOtherGenderCount(long otherGenderCount) {
        this.otherGenderCount = otherGenderCount;
    }

    public double getGenderRatio() {
        return genderRatio;
    }

    public void setGenderRatio(double genderRatio) {
        this.genderRatio = genderRatio;
    }

    public long getLiterateCount() {
        return literateCount;
    }

    public void setLiterateCount(long literateCount) {
        this.literateCount = literateCount;
    }

    public long getIlliterateCount() {
        return illiterateCount;
    }

    public void setIlliterateCount(long illiterateCount) {
        this.illiterateCount = illiterateCount;
    }

    public double getLiteracyRate() {
        return literacyRate;
    }

    public void setLiteracyRate(double literacyRate) {
        this.literacyRate = literacyRate;
    }

    public Map<String, Long> getOccupationBreakdown() {
        return occupationBreakdown;
    }

    public void setOccupationBreakdown(Map<String, Long> occupationBreakdown) {
        this.occupationBreakdown = occupationBreakdown;
    }

    public Map<String, Long> getNationalityBreakdown() {
        return nationalityBreakdown;
    }

    public void setNationalityBreakdown(Map<String, Long> nationalityBreakdown) {
        this.nationalityBreakdown = nationalityBreakdown;
    }
}
