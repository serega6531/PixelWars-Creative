package ru.serega6531.pixelwars.creative.model.response;

import ru.serega6531.pixelwars.creative.model.Pixel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InitialResponse {

    private int sizeX;
    private int sizeY;
    private int backgroundColor;
    private Map<Integer, Integer> pixels;

    private int colorsAmount;
    private Map<Integer, Integer> colors = new HashMap<>();

    private int cooldown;

    public InitialResponse(int sizeX, int sizeY, int backgroundColor, List<Pixel> pixelsList, List<Integer> colors, int cooldown) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.backgroundColor = backgroundColor;
        this.cooldown = cooldown;

        pixels = pixelsList.stream()
                .collect(Collectors.toMap(
                        pixel -> pixel.getPosition().getX() * sizeX + pixel.getPosition().getY(),
                        Pixel::getColor));

        this.colorsAmount = colors.size();
        for(int i = 0; i < colors.size(); i++){
            this.colors.put(i, colors.get(i));
        }
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

    public Map<Integer, Integer> getColors() {
        return colors;
    }

    public int getColorsAmount() {
        return colorsAmount;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getCooldown() {
        return cooldown;
    }
}
