package ru.serega6531.pixelwars.creative.model.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VKTokenInfo {

    private String token;
    private int expires;
    private int userId;

    @JsonProperty("access_token")
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @JsonProperty("expires_in")
    public void setExpires(int expires) {
        this.expires = expires;
    }

    public int getExpires() {
        return expires;
    }

    @JsonProperty("user_id")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "VKTokenInfo{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                ", userId=" + userId +
                '}';
    }

}
