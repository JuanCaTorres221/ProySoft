package com.kairo_emocion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}