package com.raport.app.repository;

import com.raport.app.entity.SystemLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogsRepository extends JpaRepository<SystemLogs, Integer> {
}