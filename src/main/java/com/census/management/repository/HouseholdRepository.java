package com.census.management.repository;

import com.census.management.entity.Household;
import com.census.management.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {

    Page<Household> findByRegion(Region region, Pageable pageable);

    Page<Household> findByRegionId(Long regionId, Pageable pageable);

    @Query("SELECT h FROM Household h WHERE " +
           "(:regionId IS NULL OR h.region.id = :regionId) AND " +
           "(:keyword IS NULL OR LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(h.id AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Household> searchHouseholds(@Param("regionId") Long regionId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    long countByRegion(Region region);

    long countByRegionId(Long regionId);

    List<Household> findTop5ByRegionOrderByCreatedAtDesc(Region region);

    List<Household> findTop5ByOrderByCreatedAtDesc();
}
