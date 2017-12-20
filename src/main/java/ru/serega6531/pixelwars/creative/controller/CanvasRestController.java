package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;

import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController
public class CanvasRestController {

    private final DrawingCanvas canvas;
    private final UserController userController;

    @Autowired
    public CanvasRestController(DrawingCanvas canvas, UserController userController) {
        this.canvas = canvas;
        this.userController = userController;
    }

    @PostMapping("/canvas/updatePixel")
    public JsonResponse updatePixel(@RequestBody Pixel pixel, HttpSession session) {
        if (session.isNew()) {
            return JsonResponse.UNAUTHORIZED;
        }

        User user = userController.getUser(session);
        if (user.isBanned()) {
            return JsonResponse.BANNED;
        }

        JsonResponse response = canvas.updatePixel(pixel);

        if(response.isSuccess()){
            user.setLastDraw(new Date());
            userController.saveUser(user);
        }

        return response;
    }

}
