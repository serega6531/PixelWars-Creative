package ru.serega6531.pixelwars.creative.model.response;

import java.util.Date;

public class CooldownResponse extends JsonResponse {

    private Date lastDraw;

    public CooldownResponse(Date lastDraw) {
        super();
        this.lastDraw = lastDraw;
    }

    public Date getLastDraw() {
        return lastDraw;
    }
}
