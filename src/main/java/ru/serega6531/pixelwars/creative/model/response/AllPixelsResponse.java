package ru.serega6531.pixelwars.creative.model.response;

import ru.serega6531.pixelwars.creative.model.Pixel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllPixelsResponse {

    private int sizeX;
    private int sizeY;
    private int backgroundColor;
    private Map<Integer, Integer> pixels;

    public AllPixelsResponse(int sizeX, int sizeY, int backgroundColor, List<Pixel> pixelsList) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.backgroundColor = backgroundColor;

        pixels = pixelsList.stream()
                .collect(Collectors.toMap(
                        pixel -> pixel.getPosition().getX() * sizeX + pixel.getPosition().getY(),
                        Pixel::getColor));
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Map<Integer, Integer> getPixels() {
        return pixels;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
