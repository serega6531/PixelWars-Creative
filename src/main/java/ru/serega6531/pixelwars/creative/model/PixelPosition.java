package ru.serega6531.pixelwars.creative.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PixelPosition implements Serializable {

    private Integer x;
    private Integer y;

    public PixelPosition(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public PixelPosition() {
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
