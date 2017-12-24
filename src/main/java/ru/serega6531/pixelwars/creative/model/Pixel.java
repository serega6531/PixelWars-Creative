package ru.serega6531.pixelwars.creative.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "canvas")
@Entity
public class Pixel implements Serializable {

    @EmbeddedId
    private PixelPosition position;
    private int color;

    public Pixel(PixelPosition position, int color) {
        this.position = position;
        this.color = color;
    }

    public Pixel() {
    }

    public PixelPosition getPosition() {
        return position;
    }

    public void setPosition(PixelPosition position) {
        this.position = position;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
