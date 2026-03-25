package com.raport.app.service;

import com.raport.app.entity.Dispatcher;
import com.raport.app.repository.DispatcherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatcherService {

    private final DispatcherRepository dispatcherRepository;

    public DispatcherService(DispatcherRepository dispatcherRepository) {
        this.dispatcherRepository = dispatcherRepository;
    }

    public List<Dispatcher> getAll() {
        return dispatcherRepository.findAll();
    }

    public Dispatcher getById(Integer id) {
        return dispatcherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispatcher not found"));
    }

    public Dispatcher save(Dispatcher dispatcher) {
        return dispatcherRepository.save(dispatcher);
    }

    public void delete(Integer id) {
        dispatcherRepository.deleteById(id);
    }
}