package ru.serega6531.pixelwars.creative.model.response;

import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PixelsResponse {

    private int sizeX;
    private int sizeY;
    private Map<Integer, Integer> pixels;

    public PixelsResponse(int sizeX, int sizeY, List<Pixel> pixelsList) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        pixels = pixelsList.stream()
                .collect(Collectors.toMap(
                        pixel -> pixel.getPosition().getX() * sizeX + pixel.getPosition().getY(),
                        Pixel::getColor));
    }

    public PixelsResponse(int sizeX, int sizeY, Map<PixelPosition, Integer> pixelsMap) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        pixels = pixelsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        ent -> ent.getKey().getX() * sizeX + ent.getKey().getY(),
                        Map.Entry::getValue));
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
}
