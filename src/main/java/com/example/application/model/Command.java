package com.example.application.model;

public enum Command {

    LIGHT_ON("ON"),

    LIGHT_OFF("OFF"),

    BEDROOM_ON("CUARTO ON"),

    BEDROOM_OFF("CUARTO OFF"),

    ENTRANCE_ON("EXTERNA ON"),

    ENTRANCE_OFF("EXTERNA OFF"),

    DOOR_OPEN("DOOR ON"),

    DOOR_CLOSE("DOOR OFF"),

    GARAGE_OPEN("GARAGE ON"),

    GARAGE_CLOSE("GARAGE OFF"),

    FAN_ON("MOTOR ON"),

    FAN_OFF("MOTOR OFF");

    private final String code;

    Command(String code) {

        this.code = code;

    }

    public String getCode() {

        return code;

    }

}

