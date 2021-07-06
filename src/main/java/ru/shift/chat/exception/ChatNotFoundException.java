package ru.shift.chat.exception;

public class ChatNotFoundException extends Exception{
    @Override
    public String getMessage() {
        return "Chat not found";
    }
}
