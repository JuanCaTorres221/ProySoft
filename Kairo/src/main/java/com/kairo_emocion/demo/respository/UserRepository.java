package com.kairo_emocion.demo.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> { }
