package com.census.management.service;

import com.census.management.dto.AgeGroupReportDto;
import com.census.management.dto.SummaryReportDto;
import com.census.management.entity.Person;
import com.census.management.entity.Region;
import com.census.management.repository.HouseholdRepository;
import com.census.management.repository.PersonRepository;
import com.census.management.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final PersonRepository personRepository;
    private final HouseholdRepository householdRepository;
    private final RegionRepository regionRepository;

    private static final List<String> STANDARD_GENDERS = List.of("Male", "Female", "male", "female");

    public ReportService(PersonRepository personRepository,
                         HouseholdRepository householdRepository,
                         RegionRepository regionRepository) {
        this.personRepository = personRepository;
        this.householdRepository = householdRepository;
        this.regionRepository = regionRepository;
    }

    public SummaryReportDto getSummaryReport(Long regionId) {
        SummaryReportDto summary = new SummaryReportDto();
        summary.setRegionId(regionId);

        if (regionId != null) {
            Region region = regionRepository.findById(regionId).orElse(null);
            if (region != null) {
                summary.setRegionName(region.getFullName());
            }
        } else {
            summary.setRegionName("All Regions (National)");
        }

        long totalPopulation;
        long totalHouseholds;
        long maleCount;
        long femaleCount;
        long otherCount;
        long literateCount;

        if (regionId != null) {
            totalPopulation = personRepository.countByHouseholdRegionId(regionId);
            totalHouseholds = householdRepository.countByRegionId(regionId);
            maleCount = personRepository.countByHouseholdRegionIdAndGenderIgnoreCase(regionId, "Male");
            femaleCount = personRepository.countByHouseholdRegionIdAndGenderIgnoreCase(regionId, "Female");
            otherCount = personRepository.countByHouseholdRegionIdAndGenderIgnoreCaseNotIn(regionId, STANDARD_GENDERS);
            literateCount = personRepository.countByHouseholdRegionIdAndEducationNotIgnoreCase(regionId, "Illiterate");
        } else {
            totalPopulation = personRepository.count();
            totalHouseholds = householdRepository.count();
            maleCount = personRepository.countByGenderIgnoreCase("Male");
            femaleCount = personRepository.countByGenderIgnoreCase("Female");
            otherCount = personRepository.countByGenderIgnoreCaseNotIn(STANDARD_GENDERS);
            literateCount = personRepository.countByEducationNotIgnoreCase("Illiterate");
        }

        long illiterateCount = Math.max(0, totalPopulation - literateCount);

        // Gender ratio: Females per 1000 males
        double genderRatio = 0.0;
        if (maleCount > 0) {
            genderRatio = BigDecimal.valueOf((femaleCount * 1000.0) / maleCount)
                    .setScale(1, RoundingMode.HALF_UP).doubleValue();
        } else if (femaleCount > 0) {
            genderRatio = 1000.0;
        }

        // Literacy rate percentage
        double literacyRate = 0.0;
        if (totalPopulation > 0) {
            literacyRate = BigDecimal.valueOf((literateCount * 100.0) / totalPopulation)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        summary.setTotalPopulation(totalPopulation);
        summary.setTotalHouseholds(totalHouseholds);
        summary.setMaleCount(maleCount);
        summary.setFemaleCount(femaleCount);
        summary.setOtherGenderCount(otherCount);
        summary.setGenderRatio(genderRatio);
        summary.setLiterateCount(literateCount);
        summary.setIlliterateCount(illiterateCount);
        summary.setLiteracyRate(literacyRate);

        // Occupation breakdown
        Map<String, Long> occupationMap = new HashMap<>();
        List<Object[]> occupations = personRepository.countByOccupation(regionId);
        for (Object[] row : occupations) {
            String occ = (String) row[0];
            Long cnt = (Long) row[1];
            occupationMap.put(occ != null ? occ : "Unspecified", cnt);
        }
        summary.setOccupationBreakdown(occupationMap);

        // Nationality breakdown
        Map<String, Long> nationalityMap = new HashMap<>();
        List<Object[]> nationalities = personRepository.countByNationality(regionId);
        for (Object[] row : nationalities) {
            String nat = (String) row[0];
            Long cnt = (Long) row[1];
            nationalityMap.put(nat != null ? nat : "Indian", cnt);
        }
        summary.setNationalityBreakdown(nationalityMap);

        return summary;
    }

    public AgeGroupReportDto getAgeGroupReport(Long regionId) {
        String regionName = "All Regions";
        if (regionId != null) {
            Region region = regionRepository.findById(regionId).orElse(null);
            if (region != null) {
                regionName = region.getFullName();
            }
        }

        List<Person> people = (regionId != null) ?
                personRepository.findByHouseholdRegionId(regionId) : personRepository.findAll();

        long under18 = 0;
        long age18to35 = 0;
        long age36to60 = 0;
        long above60 = 0;

        for (Person p : people) {
            int age = p.getAge();
            if (age < 18) {
                under18++;
            } else if (age <= 35) {
                age18to35++;
            } else if (age <= 60) {
                age36to60++;
            } else {
                above60++;
            }
        }

        return new AgeGroupReportDto(regionId, regionName, under18, age18to35, age36to60, above60, people.size());
    }

    public String exportToCsv(Long regionId) {
        List<Person> persons = (regionId != null) ?
                personRepository.findByHouseholdRegionId(regionId) : personRepository.findAll();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // CSV Header
        pw.println("Person ID,ID Number,Full Name,Gender,Age,Date of Birth,Relationship,Marital Status,Education,Occupation,Nationality,Household ID,Address,City,District,State,Created At");

        for (Person p : persons) {
            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    escapeCsv(String.valueOf(p.getId())),
                    escapeCsv(p.getIdNumber()),
                    escapeCsv(p.getName()),
                    escapeCsv(p.getGender()),
                    p.getAge(),
                    p.getDob() != null ? p.getDob().toString() : "",
                    escapeCsv(p.getRelation()),
                    escapeCsv(p.getMaritalStatus()),
                    escapeCsv(p.getEducation()),
                    escapeCsv(p.getOccupation()),
                    escapeCsv(p.getNationality()),
                    p.getHousehold() != null ? String.valueOf(p.getHousehold().getId()) : "",
                    p.getHousehold() != null ? escapeCsv(p.getHousehold().getAddress()) : "",
                    p.getHousehold() != null && p.getHousehold().getRegion() != null ? escapeCsv(p.getHousehold().getRegion().getCity()) : "",
                    p.getHousehold() != null && p.getHousehold().getRegion() != null ? escapeCsv(p.getHousehold().getRegion().getDistrict()) : "",
                    p.getHousehold() != null && p.getHousehold().getRegion() != null ? escapeCsv(p.getHousehold().getRegion().getState()) : "",
                    p.getCreatedAt() != null ? p.getCreatedAt().toString() : ""
            );
        }

        pw.flush();
        return sw.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
