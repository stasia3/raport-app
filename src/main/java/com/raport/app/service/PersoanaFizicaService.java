package com.raport.app.service;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.repository.PersoanaFizicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersoanaFizicaService {

    private final PersoanaFizicaRepository persoanaFizicaRepository;

    public PersoanaFizicaService(PersoanaFizicaRepository persoanaFizicaRepository) {
        this.persoanaFizicaRepository = persoanaFizicaRepository;
    }

    public List<PersoanaFizica> getAll() {
        return persoanaFizicaRepository.findAll();
    }

    public PersoanaFizica getById(Integer id) {
        return persoanaFizicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persoana fizica not found"));
    }

    public PersoanaFizica save(PersoanaFizica persoanaFizica) {
        return persoanaFizicaRepository.save(persoanaFizica);
    }

    public void delete(Integer id) {
        persoanaFizicaRepository.deleteById(id);
    }
}
