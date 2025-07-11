package com.example.application.serial;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Component

@ConfigurationProperties(prefix = "esp32")

public class SerialConfig {

    private String port;

    private int baudrate;

    public String getPort() {

        return port;

    }

    public void setPort(String port) {

        this.port = port;

    }

    public int getBaudrate() {

        return baudrate;

    }

    public void setBaudrate(int baudrate) {

        this.baudrate = baudrate;

    }

}


