package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.repository.UserRepository;

@RestController
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable int id){
        return repository.findOne(id);
    }

}
