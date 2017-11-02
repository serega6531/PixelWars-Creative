package ru.serega6531.pixelwars.creative.model;

public class PixelUpdateResponse {

    private boolean success;
    private String reason;

    public static final PixelUpdateResponse SUCCESS = new PixelUpdateResponse(true, "");
    public static final PixelUpdateResponse ILLEGAL_COORDINATES = new PixelUpdateResponse(false, "illegal_coordinates");
    public static final PixelUpdateResponse UNAUTHORIZED = new PixelUpdateResponse(false, "unauthorized");
    public static final PixelUpdateResponse SESSION_OUTDATED = new PixelUpdateResponse(false, "session_outdated");

    private PixelUpdateResponse(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
