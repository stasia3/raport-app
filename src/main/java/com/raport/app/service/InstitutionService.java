package com.raport.app.service;

import com.raport.app.entity.Institution;
import com.raport.app.repository.InstitutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> getAll() {
        return institutionRepository.findAll();
    }

    public Institution getById(Integer id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found"));
    }

    public Institution save(Institution institution) {
        return institutionRepository.save(institution);
    }

    public void delete(Integer id) {
        institutionRepository.deleteById(id);
    }
}