package com.raport.app.repository;

import com.raport.app.entity.GlobalAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalAdminRepository extends JpaRepository<GlobalAdmin, Integer> {
}