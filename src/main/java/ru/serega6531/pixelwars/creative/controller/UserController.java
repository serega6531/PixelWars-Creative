package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.response.RestResponse;
import ru.serega6531.pixelwars.creative.repository.UserRepository;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user/get/{id}")
    public User getUser(@PathVariable int id){
        return repository.findOne(id);
    }

    @PostMapping("/user/ban/{id}")
    public RestResponse banUser(@PathVariable int id, HttpSession session){
        if(session.isNew()){
            return RestResponse.UNAUTHORIZED;
        }

        if(((long) session.getAttribute("expires_at")) >= System.currentTimeMillis() / 1000L){
            session.invalidate();
            return RestResponse.SESSION_OUTDATED;
        }

        User admin = getUser((Integer) session.getAttribute("vk_id"));
        if(!admin.isAdmin()){
            return RestResponse.INSUFFICIENT_PRIVILEGES;
        }

        User target = repository.findOne(id);

        if(target == null){
            return RestResponse.USER_NOT_EXISTS;
        }

        if(target.isAdmin()){
            return RestResponse.CANNOT_BE_BANNED;
        }

        target.setBanned(true);
        repository.save(target);
        return RestResponse.SUCCESS;
    }

    @PostMapping("/user/unban/{id}")
    public RestResponse unbanUser(@PathVariable int id, HttpSession session){
        if(session.isNew()){
            return RestResponse.UNAUTHORIZED;
        }

        if(((long) session.getAttribute("expires_at")) >= System.currentTimeMillis() / 1000L){
            session.invalidate();
            return RestResponse.SESSION_OUTDATED;
        }

        User admin = getUser((Integer) session.getAttribute("vk_id"));
        if(!admin.isAdmin()){
            return RestResponse.INSUFFICIENT_PRIVILEGES;
        }

        User target = repository.findOne(id);

        if(target == null){
            return RestResponse.USER_NOT_EXISTS;
        }

        target.setBanned(false);
        repository.save(target);
        return RestResponse.SUCCESS;
    }

}
