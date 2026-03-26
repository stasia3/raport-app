package com.raport.app.repository;


import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersoanaFizicaRepository extends JpaRepository<PersoanaFizica, Integer> {
    Optional<PersoanaFizica> findByUser(User user);
}