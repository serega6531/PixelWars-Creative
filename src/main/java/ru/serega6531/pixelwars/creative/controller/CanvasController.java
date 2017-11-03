package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.DrawingCanvas;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.RestResponse;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CanvasController {

    private final DrawingCanvas canvas;
    private final CanvasRepository repository;

    @Autowired
    public CanvasController(DrawingCanvas canvas, CanvasRepository repository) {
        this.canvas = canvas;
        this.repository = repository;
    }

    @PostMapping("/canvas/updatePixel")
    public RestResponse updatePixel(@RequestBody Pixel pixel, HttpSession session){
        if(session.isNew()){
            return RestResponse.UNAUTHORIZED;
        }

        if(((long) session.getAttribute("expires_at")) >= System.currentTimeMillis() / 1000L){
            return RestResponse.SESSION_OUTDATED;
        }

        return canvas.updatePixel(pixel);
    }

    @GetMapping("/canvas/getAllPixels")
    public Map<PixelPosition, Integer> getAllPixels(){
        return repository.findAll().stream().collect(Collectors.toMap(Pixel::getPosition, Pixel::getColor));
    }

}
