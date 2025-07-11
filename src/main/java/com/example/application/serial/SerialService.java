package com.example.application.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.io.*;

import java.util.Arrays;

import java.util.function.Consumer;

public class SerialService {

    private SerialPort port;

    private BufferedReader reader;

    private OutputStream writer;

    private Thread readerThread;

    private volatile boolean running = false;

    private Consumer<String> onLineReceived;

    public SerialService(Consumer<String> onLineReceived) {

        this.onLineReceived = onLineReceived;

    }

    public String[] listPorts() {

        return Arrays.stream(SerialPort.getCommPorts())

                .map(SerialPort::getSystemPortName)

                .toArray(String[]::new);

    }

    public boolean connect(String portName, int baudRate) {

        System.out.println("[SerialService] Intentando conectar a: " + portName + " @ " + baudRate + " bps");

        port = SerialPort.getCommPort(portName);

        port.setBaudRate(baudRate);

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0); // similar a timeout=1

        if (port.openPort()) {

            try {

                reader = new BufferedReader(new InputStreamReader(port.getInputStream()));

                writer = port.getOutputStream();

                running = true;

                readerThread = new Thread(this::listen);

                readerThread.setDaemon(true);

                readerThread.start();

                System.out.println("[SerialService] Conexión establecida con: " + portName);

                return true;

            } catch (Exception e) {

                System.err.println("[SerialService] Error al abrir puerto: " + e.getMessage());

            }

        } else {

            System.err.println("[SerialService] No se pudo abrir el puerto: " + portName);

        }

        return false;

    }

    private void listen() {

        try {

            String line;

            while (running && (line = reader.readLine()) != null) {

                line = line.trim();

                System.out.println("[ESP32 → PC] " + line);

                if (onLineReceived != null && !line.isEmpty()) {

                    onLineReceived.accept(line);

                }

            }

        } catch (Exception e) {

            System.err.println("[SerialService] Error leyendo puerto: " + e.getMessage());

        }

    }

    public void sendCommand(String cmd) {

        try {

            if (writer != null) {

                String toSend = cmd.trim() + "\n";

                writer.write(toSend.getBytes());

                writer.flush();

                System.out.println("[PC → ESP32] Enviado: " + toSend);

                Thread.sleep(20);  // delay mínimo como en Python

            } else {

                System.err.println("[SerialService] Writer es null. No se pudo enviar comando.");

            }

        } catch (Exception e) {

            System.err.println("[SerialService] Error enviando: " + e.getMessage());

        }

    }

    public void disconnect() {

        System.out.println("[SerialService] Desconectando...");

        running = false;

        try {

            if (reader != null) reader.close();

            if (writer != null) writer.close();

        } catch (IOException ignored) {}

        if (port != null && port.isOpen()) {

            port.closePort();

        }

        System.out.println("[SerialService] Desconexión completa.");

    }

    public boolean isConnected() {

        return port != null && port.isOpen();

    }

}

