package com.raport.app.repository;


import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.Post;
import com.raport.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersoanaFizicaRepository extends JpaRepository<PersoanaFizica, Integer> {
    Optional<PersoanaFizica> findByUser(User user);


}