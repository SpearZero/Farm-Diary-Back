package com.farmdiary.api.repository.user;

import com.farmdiary.api.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // Optional<User> findByNickname(String nickName);

    Boolean existsByNickname(String nickName);
}
