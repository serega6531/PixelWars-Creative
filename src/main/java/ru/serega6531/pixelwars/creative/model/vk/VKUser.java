package ru.serega6531.pixelwars.creative.model.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VKUser {

    public static VKUser ANONYMOUS = new VKUser(-1, "Anonymous", "user");

    private int id;
    private String firstName;
    private String lastName;

    public VKUser(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected VKUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
