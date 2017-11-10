package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.DrawingCanvas;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.response.PixelsResponse;
import ru.serega6531.pixelwars.creative.model.response.RestResponse;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RestController
public class CanvasController {

    private final DrawingCanvas canvas;
    private final UserController userController;
    private final CanvasRepository repository;

    private PixelsResponse cachedChanges = null;

    @Autowired
    public CanvasController(DrawingCanvas canvas, UserController userController, CanvasRepository repository) {
        this.canvas = canvas;
        this.userController = userController;
        this.repository = repository;
    }

    @PostMapping("/canvas/updatePixel")
    public RestResponse updatePixel(@RequestBody Pixel pixel, HttpSession session){
        if(session.isNew()){
            return RestResponse.UNAUTHORIZED;
        }

        if(System.currentTimeMillis() / 1000L >= ((long) session.getAttribute("expires_at"))){
            return RestResponse.SESSION_OUTDATED;
        }

        User user = userController.getUser(session);
        if(user.isBanned()){
            return RestResponse.BANNED;
        }

        return canvas.updatePixel(pixel);
    }

    @GetMapping("/canvas/getAllPixels")
    public PixelsResponse getAllPixels(){
        return new PixelsResponse(canvas.getSizeX(), canvas.getSizeY(), repository.findAll());
    }

    @GetMapping("/canvas/getUpdates")
    public PixelsResponse getUpdates(){
        if(cachedChanges != null){
            return cachedChanges;
        } else {
            return new PixelsResponse(canvas.getSizeX(), canvas.getSizeY(), Collections.emptyMap());
        }
    }

    @Scheduled(fixedRate = 1000L)
    public void cacheUpdates(){
        cachedChanges = new PixelsResponse(canvas.getSizeX(), canvas.getSizeY(), canvas.getAndClearUpdates());
    }

}
