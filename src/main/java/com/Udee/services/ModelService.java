package com.Udee.services;

import com.Udee.exceptions.ModelNotFoundException;
import com.Udee.models.Model;
import com.Udee.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Model addModel(Model model) {
        return modelRepository.save(model);
    }

    public Page<Model> findAll(Specification<Model> spec, Pageable pageable) {
        return modelRepository.findAll(spec,pageable);
    }

    public void delete(Integer id) {
        try{
            modelRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException();
        }
    }
}
