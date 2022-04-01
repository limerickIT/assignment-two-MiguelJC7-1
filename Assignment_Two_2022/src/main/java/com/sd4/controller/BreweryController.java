/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.service.BreweryService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 7migu
 */
@RestController
@RequestMapping("/Brewery")
public class BreweryController {
    @Autowired
    private BreweryService breweryService;
    
    
 
    
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<Brewery>>> findAll()
    {
            List<EntityModel<Brewery>> beers = StreamSupport.stream(breweryService.findAll().spliterator(), false)
				.map(Brewery -> EntityModel.of(Brewery, //
						linkTo(methodOn(BreweryController.class).findOne(Brewery.getId())).withSelfRel(), //
						linkTo(methodOn(BreweryController.class).findAll()).withRel("all"))) //
				.collect(Collectors.toList());

		return ResponseEntity.ok( //
				CollectionModel.of(beers, //
						linkTo(methodOn(BreweryController.class).findAll()).withSelfRel()));
    }
    
    @GetMapping("{id}")
    ResponseEntity<EntityModel<Brewery>> findOne(@PathVariable long id) {

		return breweryService.findOne(id) //
				.map(Brewery -> EntityModel.of(Brewery, //
						linkTo(methodOn(BreweryController.class).findOne(Brewery.getId())).withSelfRel(), //
						linkTo(methodOn(BreweryController.class).findAll()).withRel("all"))) //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}
    
    @DeleteMapping(value = "/Delete/{id}")
    public void deleteBeer(@PathVariable long id) {
         breweryService.deleteByID(id);
    }
    
    @PostMapping(value = "/create")
   public void NewBrewery(@RequestBody Brewery newBrewery){
         breweryService.saveBrewery(newBrewery);
}

   
   
}
