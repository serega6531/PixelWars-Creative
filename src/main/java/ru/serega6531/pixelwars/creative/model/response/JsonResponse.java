package ru.serega6531.pixelwars.creative.model.response;

public class JsonResponse {

    private boolean success;
    private String reason;

    //BASIC RESPONSES
    public static final JsonResponse SUCCESS = new JsonResponse();
    public static final JsonResponse UNAUTHORIZED = new JsonResponse(false, "unauthorized");
    public static final JsonResponse INSUFFICIENT_PRIVILEGES = new JsonResponse(false, "insufficient_privileges");
    public static final JsonResponse BANNED = new JsonResponse(false, "banned");

    //CANVAS RESPONSES
    public static final JsonResponse ILLEGAL_COORDINATES = new JsonResponse(false, "illegal_coordinates");
    public static final JsonResponse ILLEGAL_COLOR = new JsonResponse(false, "illegal_color");
    public static final JsonResponse ON_COOLDOWN = new JsonResponse(false, "on_cooldown");

    //USER RESPONSES
    public static final JsonResponse USER_NOT_EXISTS = new JsonResponse(false, "user_not_exists");
    public static final JsonResponse CANNOT_BE_BANNED = new JsonResponse(false, "target_cannot_be_banned");

    private JsonResponse(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    JsonResponse() {
        this(true, "success");
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

    @Override
    public String toString() {
        return "JsonResponse{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                '}';
    }
}
