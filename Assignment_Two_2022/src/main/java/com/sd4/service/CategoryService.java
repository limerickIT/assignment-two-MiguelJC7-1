/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

import com.sd4.model.Category;
import com.sd4.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 7migu
 */
@Service
public class CategoryService {
        @Autowired
    private CategoryRepository CatRepo;

    public Optional<Category> findOne(Long id) {
        return CatRepo.findById(id);
    }

    public List<Category> findAll() {
        return (List<Category>) CatRepo.findAll();
    }

    public void deleteByID(long BreweryId) {
        CatRepo.deleteById(BreweryId);
    }

    public void saveBrewery(Category a) {
        CatRepo.save(a);
    }  

    public List<Category> findById(long BreweryId) {
        return CatRepo.findById(BreweryId);
    }
}
