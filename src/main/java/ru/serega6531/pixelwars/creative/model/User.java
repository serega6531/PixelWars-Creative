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
    private boolean isAdmin;
    private boolean isBanned;
    private int updates;

    public User(int vkId) {
        this.vkId = vkId;
        this.lastDraw = new Date(0);
        this.isAdmin = false;
        this.isBanned = false;
        this.updates = 0;
    }

    protected User() {
    }

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public int getUpdates() {
        return updates;
    }

    public void setUpdates(int updates) {
        this.updates = updates;
    }
}
