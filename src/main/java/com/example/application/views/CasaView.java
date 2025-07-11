package com.example.application.views;

import com.example.application.model.Command;
import com.example.application.serial.SerialService;
import com.example.application.serial.SerialConfig;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Route("8081")
public class CasaView extends VerticalLayout {

    private final SerialService serialService;
    private final SerialConfig serialConfig;
    private Button connectButton;

    public CasaView(@Autowired SerialConfig serialConfig, Button connectButton) {
        this.serialConfig = serialConfig;
        this.connectButton = connectButton;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        this.serialService = new SerialService(this::handleIncomingLine);

        add(new H2("Sistema Domótico ESP32 - Control Serial"));

        add(buildConnectionPanel());
        add(buildControlsPanel());
    }

    // Panel para conectar/desconectar
    private HorizontalLayout buildConnectionPanel() {
        connectButton = new Button("Conectar a ESP32", event -> toggleConnection());
        connectButton.setWidth("200px");

        HorizontalLayout connectionLayout = new HorizontalLayout();
        connectionLayout.setSpacing(true);
        connectionLayout.add(connectButton);

        return connectionLayout;
    }

    // Acción del botón
    private void toggleConnection() {
        if (serialService.isConnected()) {
            serialService.disconnect();
            connectButton.setText("Conectar a ESP32");
            Notification.show("✅ Desconectado de la ESP32");
        } else {
            String port = serialConfig.getPort();
            int baudrate = serialConfig.getBaudrate();

            if (port == null || port.isEmpty()) {
                Notification.show("⚠️ Error: puerto no configurado en application.properties");
                return;
            }

            boolean success = serialService.connect(port, baudrate);
            if (success) {
                connectButton.setText("Desconectar");
                Notification.show("✅ Conectado al puerto: " + port);
            } else {
                Notification.show("❌ Error al conectar al puerto: " + port);
            }
        }
    }

    // Panel con TODOS los botones de control
    private VerticalLayout buildControlsPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(true);
        panel.setSpacing(true);

        panel.add(new H2("Controles de Dispositivos"));

        FlexLayout buttonsLayout = new FlexLayout();
        buttonsLayout.getStyle().set("flex-wrap", "wrap");
        buttonsLayout.getStyle().set("gap", "10px");
        buttonsLayout.setWidthFull();

        Arrays.stream(Command.values()).forEach(cmd -> {
            Button btn = new Button(cmd.name().replace("_", " "));
            btn.setWidth("150px");
            btn.addClickListener(e -> sendCommand(cmd.getCode()));
            buttonsLayout.add(btn);
        });

        panel.add(buttonsLayout);
        return panel;
    }

    // Enviar comando
    private void sendCommand(String cmd) {
        if (serialService.isConnected()) {
            serialService.sendCommand(cmd);
            Notification.show("📤 Enviado: " + cmd);
        } else {
            Notification.show("⚠️ Conecta primero la ESP32.");
        }
    }

    // Manejar datos recibidos
    private void handleIncomingLine(String line) {
        System.out.println("[ESP32 → PC] " + line);
        Notification.show("📥 ESP32: " + line);
    }
}