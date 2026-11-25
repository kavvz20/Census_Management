package com.census.management.dto;

public class AgeGroupReportDto {

    private Long regionId;
    private String regionName;
    private long under18;
    private long age18to35;
    private long age36to60;
    private long above60;
    private long total;

    public AgeGroupReportDto() {
    }

    public AgeGroupReportDto(Long regionId, String regionName, long under18, long age18to35, long age36to60, long above60, long total) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.under18 = under18;
        this.age18to35 = age18to35;
        this.age36to60 = age36to60;
        this.above60 = above60;
        this.total = total;
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

    public long getUnder18() {
        return under18;
    }

    public void setUnder18(long under18) {
        this.under18 = under18;
    }

    public long getAge18to35() {
        return age18to35;
    }

    public void setAge18to35(long age18to35) {
        this.age18to35 = age18to35;
    }

    public long getAge36to60() {
        return age36to60;
    }

    public void setAge36to60(long age36to60) {
        this.age36to60 = age36to60;
    }

    public long getAbove60() {
        return above60;
    }

    public void setAbove60(long above60) {
        this.above60 = above60;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
