package com.raport.app.repository;

import com.raport.app.entity.Dispatcher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatcherRepository extends JpaRepository<Dispatcher, Integer> {
}