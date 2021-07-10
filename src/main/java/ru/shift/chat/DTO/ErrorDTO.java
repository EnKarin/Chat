package ru.shift.chat.DTO;

import ru.shift.chat.enums.ErrorCode;

public class ErrorDTO {
    private final String code;

    public ErrorDTO(ErrorCode errorCode) {
        this.code = errorCode.name();
    }

    public String getCode() {
        return code;
    }
}
