package ru.serega6531.pixelwars.creative.model.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VKError {

    private String error;
    private String errorDescription;

    public String getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error_description")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
