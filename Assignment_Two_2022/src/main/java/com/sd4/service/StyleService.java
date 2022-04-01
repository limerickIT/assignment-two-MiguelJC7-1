/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.service;

import com.sd4.model.Style;
import com.sd4.repository.StyleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 7migu
 */
@Service
public class StyleService {
       @Autowired
    private StyleRepository StyleRepo;

    public Optional<Style> findOne(Long id) {
        return StyleRepo.findById(id);
    }

    public List<Style> findAll() {
        return (List<Style>) StyleRepo.findAll();
    }

    public void deleteByID(long BreweryId) {
        StyleRepo.deleteById(BreweryId);
    }

    public void saveBrewery(Style a) {
        StyleRepo.save(a);
    }  

    public List<Style> findById(long StyleId) {
        return StyleRepo.findById(StyleId);
    }
}
