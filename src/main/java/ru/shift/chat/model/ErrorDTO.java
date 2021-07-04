package ru.shift.chat.model;

import ru.shift.chat.enums.ErrorCode;

public class ErrorDTO {
    private final String code;

    public ErrorDTO(ErrorCode errorCode) {
        this.code = errorCode.name();
    }
}
