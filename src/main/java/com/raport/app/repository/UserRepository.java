package com.raport.app.repository;


import com.raport.app.entity.User;
import com.raport.app.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByUserRole(UserRole type);
}