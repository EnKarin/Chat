package ru.shift.chat.model;

public class User {
    private int userId;
    private final String firstName;
    private final  String lastName;

    public User(String f, String l){
        firstName = f;
        lastName = l;
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
