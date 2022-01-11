package com.farmdiary.api.repository.user;

import com.farmdiary.api.entity.user.GrantedRole;
import com.farmdiary.api.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(GrantedRole name);
}
