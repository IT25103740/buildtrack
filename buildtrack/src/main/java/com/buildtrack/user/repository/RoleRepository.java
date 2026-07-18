package com.buildtrack.user.repository;

import com.buildtrack.user.entity.Role;
import com.buildtrack.user.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
