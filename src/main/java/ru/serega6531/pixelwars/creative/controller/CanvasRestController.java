package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.model.response.CooldownResponse;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
public class CanvasRestController {

    private final DrawingCanvas canvas;
    private final UserController userController;

    @Value("${drawing.cooldown}")
    private int cooldown;

    @Autowired
    public CanvasRestController(DrawingCanvas canvas, UserController userController) {
        this.canvas = canvas;
        this.userController = userController;
    }

    @GetMapping("/canvas/getCooldown")
    public JsonResponse getCooldown(HttpSession session) {
        if (session.isNew() || !session.getAttributeNames().hasMoreElements()) {
            return JsonResponse.UNAUTHORIZED;
        }

        User user = userController.getUser(session);
        return new CooldownResponse(user.getLastDraw());
    }

    @PostMapping("/canvas/updatePixel")
    public JsonResponse updatePixel(@RequestBody Pixel pixel, HttpSession session) {
        if (session.isNew() || !session.getAttributeNames().hasMoreElements()) {
            return JsonResponse.UNAUTHORIZED;
        }

        User user = userController.getUser(session);
        if (user.isBanned()) {
            return JsonResponse.BANNED;
        }

        Date now = new Date();
        if(now.getTime() - user.getLastDraw().getTime() < cooldown){
            return JsonResponse.ON_COOLDOWN;
        }

        JsonResponse response = canvas.updatePixel(pixel);

        if(response.isSuccess()){
            user.setLastDraw(now);
            userController.saveUser(user);
        }

        return response;
    }

}
