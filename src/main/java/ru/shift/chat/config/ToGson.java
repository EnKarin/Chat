package ru.shift.chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.shift.chat.DTO.ErrorDTO;
import ru.shift.chat.enums.ErrorCode;

public class ToGson {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String ErrorToGson(ErrorCode code){
        return gson.toJson(new ErrorDTO(code));
    }
}
