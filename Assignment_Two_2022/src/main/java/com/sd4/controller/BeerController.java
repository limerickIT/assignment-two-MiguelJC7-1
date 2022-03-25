
package com.sd4.controller;

import com.sd4.model.Beer;
import com.sd4.service.BeerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/Beers")
public class BeerController {
    
    @Autowired
    private BeerService BeerService;
    
    BeerController(BeerService repository) {
		this.BeerService = repository;
	}
 
    
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<Beer>>> findAll()
    {
            List<EntityModel<Beer>> employees = StreamSupport.stream(BeerService.findAll().spliterator(), false)
				.map(beer -> EntityModel.of(beer, //
						linkTo(methodOn(BeerController.class).findOne(beer.getId())).withSelfRel(), //
						linkTo(methodOn(BeerController.class).findAll()).withRel("all"))) //
				.collect(Collectors.toList());

		return ResponseEntity.ok( //
				CollectionModel.of(employees, //
						linkTo(methodOn(BeerController.class).findAll()).withSelfRel()));
    }
    
    @GetMapping("{id}")
    ResponseEntity<EntityModel<Beer>> findOne(@PathVariable long id) {

		return BeerService.findOne(id) //
				.map(beer -> EntityModel.of(beer, //
						linkTo(methodOn(BeerController.class).findOne(beer.getId())).withSelfRel(), //
						linkTo(methodOn(BeerController.class).findAll()).withRel("all"))) //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}
//    public ResponseEntity<Beer> getBeerByID(@PathVariable("id") long id)
//    {
//        Optional<Beer> b = BeerService.findOne(id);
//        if(!b.isPresent()){
//            return new ResponseEntity(HttpStatus.NOT_FOUND);
//        }else{
//            return ResponseEntity.ok(b.get());
//        }
//    }

}
