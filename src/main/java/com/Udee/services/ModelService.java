package com.Udee.services;

import com.Udee.exceptions.ModelNotFoundException;
import com.Udee.models.Model;
import com.Udee.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelService {
    private final ModelRepository modelRepository;

    @Autowired
    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public Model findById(Integer modelId) {
        return modelRepository.findById(modelId).orElseThrow(ModelNotFoundException::new);
    }
}
