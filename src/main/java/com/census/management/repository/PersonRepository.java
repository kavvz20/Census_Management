package com.census.management.repository;

import com.census.management.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByIdNumber(String idNumber);

    boolean existsByIdNumber(String idNumber);

    boolean existsByIdNumberAndIdNot(String idNumber, Long id);

    List<Person> findByHouseholdId(Long householdId);

    Page<Person> findByHouseholdId(Long householdId, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE " +
           "(:regionId IS NULL OR p.household.region.id = :regionId) AND " +
           "(:householdId IS NULL OR p.household.id = :householdId) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.idNumber) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Person> searchPersons(@Param("regionId") Long regionId,
                               @Param("householdId") Long householdId,
                               @Param("name") String name,
                               Pageable pageable);

    // Derived Query Methods for Exact & Clean Counting
    long countByHouseholdRegionId(Long regionId);

    long countByGenderIgnoreCase(String gender);
    long countByHouseholdRegionIdAndGenderIgnoreCase(Long regionId, String gender);

    long countByGenderIgnoreCaseNotIn(Collection<String> genders);
    long countByHouseholdRegionIdAndGenderIgnoreCaseNotIn(Long regionId, Collection<String> genders);

    long countByEducationNotIgnoreCase(String education);
    long countByHouseholdRegionIdAndEducationNotIgnoreCase(Long regionId, String education);

    @Query("SELECT p.occupation, COUNT(p) FROM Person p WHERE (:regionId IS NULL OR p.household.region.id = :regionId) GROUP BY p.occupation")
    List<Object[]> countByOccupation(@Param("regionId") Long regionId);

    @Query("SELECT p.nationality, COUNT(p) FROM Person p WHERE (:regionId IS NULL OR p.household.region.id = :regionId) GROUP BY p.nationality")
    List<Object[]> countByNationality(@Param("regionId") Long regionId);

    List<Person> findByHouseholdRegionId(Long regionId);
}
