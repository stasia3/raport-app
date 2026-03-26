package com.raport.app.service;

import com.raport.app.entity.GlobalAdmin;
import com.raport.app.repository.GlobalAdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalAdminService {

    private final GlobalAdminRepository globalAdminRepository;

    public GlobalAdminService(GlobalAdminRepository globalAdminRepository) {
        this.globalAdminRepository = globalAdminRepository;
    }

    public List<GlobalAdmin> getAll() {
        return globalAdminRepository.findAll();
    }

    public GlobalAdmin getById(Integer id) {
        return globalAdminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Global admin not found"));
    }

    public GlobalAdmin save(GlobalAdmin globalAdmin) {
        return globalAdminRepository.save(globalAdmin);
    }

    public void delete(Integer id) {
        globalAdminRepository.deleteById(id);
    }
}