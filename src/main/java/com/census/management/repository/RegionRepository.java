package com.census.management.repository;

import com.census.management.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsByStateIgnoreCaseAndDistrictIgnoreCaseAndCityIgnoreCase(String state, String district, String city);

    boolean existsByStateIgnoreCaseAndDistrictIgnoreCaseAndCityIgnoreCaseAndIdNot(String state, String district, String city, Long id);

    Optional<Region> findByStateIgnoreCaseAndDistrictIgnoreCaseAndCityIgnoreCase(String state, String district, String city);

    List<Region> findAllByOrderByStateAscDistrictAscCityAsc();

    Page<Region> findByStateContainingIgnoreCaseOrDistrictContainingIgnoreCaseOrCityContainingIgnoreCase(
            String state, String district, String city, Pageable pageable);
}
