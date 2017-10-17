package ru.serega6531.pixelwars.creative.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "users")
@Entity
public class User implements Serializable {

    @Id
    private int vkId;
    private Date lastDraw;

    protected User(){}

    public int getVkId() {
        return vkId;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public Date getLastDraw() {
        return lastDraw;
    }

    public void setLastDraw(Date lastDraw) {
        this.lastDraw = lastDraw;
    }
}
