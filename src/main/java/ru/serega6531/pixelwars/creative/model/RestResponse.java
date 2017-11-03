package ru.serega6531.pixelwars.creative.model;

public class RestResponse {

    private boolean success;
    private String reason;

    //BASIC RESPONSES
    public static final RestResponse SUCCESS = new RestResponse(true, "ыгссуыы");
    public static final RestResponse UNAUTHORIZED = new RestResponse(false, "unauthorized");
    public static final RestResponse SESSION_OUTDATED = new RestResponse(false, "session_outdated");
    public static final RestResponse INSUFFICIENT_PRIVILEGES = new RestResponse(false, "insufficient_privileges");

    //CANVAS RESPONSES
    public static final RestResponse ILLEGAL_COORDINATES = new RestResponse(false, "illegal_coordinates");

    //USER RESPONSES
    public static final RestResponse USER_NOT_EXISTS = new RestResponse(false, "user_not_exists");
    public static final RestResponse CANNOT_BE_BANNED = new RestResponse(false, "target_cannot_be_banned");

    private RestResponse(boolean success, String reason) {
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
