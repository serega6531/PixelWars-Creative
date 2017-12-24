package ru.serega6531.pixelwars.creative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;

import java.util.List;

@Service
public class DrawingCanvas {

    @Value("${drawing.canvas-size.x}")
    private int canvasSizeX;

    @Value("${drawing.canvas-size.y}")
    private int canvasSizeY;

    @Value("${drawing.background-color}")
    private int backgroundColor;

    @Value("#{'${drawing.colors}'.split(',')}")
    private List<Integer> colors;

    private final CanvasRepository repository;
    private final PixelsSubscriptionService subscriptionService;

    @Autowired
    public DrawingCanvas(CanvasRepository repository, PixelsSubscriptionService subscriptionService) {
        this.repository = repository;
        this.subscriptionService = subscriptionService;
    }

    public JsonResponse updatePixel(Pixel pixel) {
        PixelPosition position = pixel.getPosition();

        if (position.getX() < 0 || position.getY() < 0 ||
                position.getX() >= canvasSizeX || position.getY() >= canvasSizeY)
            return JsonResponse.ILLEGAL_COORDINATES;

        if (pixel.getColor() < 0 || pixel.getColor() >= colors.size()) {
            return JsonResponse.ILLEGAL_COLOR;
        }

        pixel.setColor(colors.get(pixel.getColor()));
        repository.save(pixel);
        subscriptionService.broadcastUpdatedPixel(pixel);

        return JsonResponse.SUCCESS;
    }

    public int getSizeX() {
        return canvasSizeX;
    }

    public int getSizeY() {
        return canvasSizeY;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public List<Pixel> getPixels() {
        return repository.findAll();
    }

    public Pixel getPixel(PixelPosition position) {
        return repository.getOne(position);
    }

    public List<Integer> getColors() {
        return colors;
    }
}
