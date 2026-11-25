package com.census.management.repository;

import com.census.management.entity.Region;
import com.census.management.entity.Role;
import com.census.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    List<User> findByRole(Role role);

    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByRegion(Region region);

    Optional<User> findByIdAndRole(Long id, Role role);

    Page<User> findByRoleAndUsernameContainingIgnoreCase(Role role, String username, Pageable pageable);
}
