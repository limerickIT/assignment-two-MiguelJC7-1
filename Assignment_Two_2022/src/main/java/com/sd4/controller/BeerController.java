
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Beers")
public class BeerController {
    
    @Autowired
    private BeerService BeerService;
    
    @GetMapping("")
    public List<Beer> getallBeers()
    {
        return BeerService.findAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<Beer> getBeerByID(@PathVariable long id)
    {
        Optional<Beer> b = BeerService.findOne(id);
        if(!b.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }else{
            return ResponseEntity.ok(b.get());
        }
    }

}
