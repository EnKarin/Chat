package ru.shift.chat.model;

import javax.validation.constraints.NotBlank;

public class User {
    private int userId;
    @NotBlank
    private final String firstName;
    @NotBlank
    private final  String lastName;

    public User(String f, String l){
        firstName = f;
        lastName = l;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
