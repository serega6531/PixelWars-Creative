package ru.serega6531.pixelwars.creative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.response.RestResponse;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;

@Component
public class DrawingCanvas {

    @Value("${drawing.canvas-size.x}")
    private int canvasSizeX;

    @Value("${drawing.canvas-size.y}")
    private int canvasSizeY;

    private final CanvasRepository repository;

    @Autowired
    public DrawingCanvas(CanvasRepository repository) {
        this.repository = repository;
    }

    public RestResponse updatePixel(Pixel pixel){
        PixelPosition position = pixel.getPosition();

        if(position.getX() >= canvasSizeX || position.getY() >= canvasSizeY)
            return RestResponse.ILLEGAL_COORDINATES;

        repository.save(pixel);

        return RestResponse.SUCCESS;
    }

    public int getSizeX() {
        return canvasSizeX;
    }

    public int getSizeY() {
        return canvasSizeY;
    }
}
