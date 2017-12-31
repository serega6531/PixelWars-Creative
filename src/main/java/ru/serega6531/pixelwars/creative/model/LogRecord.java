package ru.serega6531.pixelwars.creative.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "logs")
@Entity
public class LogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int userId;
    private PixelPosition position;
    private int color;
    private Date date;

    public LogRecord(int userId, PixelPosition position, int color, Date date) {
        this.userId = userId;
        this.position = position;
        this.color = color;
        this.date = date;
    }

    protected LogRecord() {
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public PixelPosition getPosition() {
        return position;
    }

    public int getColor() {
        return color;
    }

    public Date getDate() {
        return date;
    }
}
