package ru.shift.chat.exception;

public class ConnectionNotFoundException extends Exception{
    @Override
    public String getMessage() {
        return "Connection not found";
    }
}
