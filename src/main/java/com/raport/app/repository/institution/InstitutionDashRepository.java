package com.raport.app.repository.institution;

import com.raport.app.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstitutionDashRepository extends JpaRepository<Institution, Integer> {

    Optional<Institution> findByUserId(Integer userId);
}