package co.scastillos.ui;


import co.scastillos.dto.SimulatedDataDto;
import co.scastillos.dto.SimulatedStatusDto;
import co.scastillos.util.DeviceSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class DeviceSimulatorUI extends JFrame {

    private JTextField deviceNameField;
    private JTextField dataRateField;
    private JTextArea monitoringArea;
    private JTextArea statusArea;

    public DeviceSimulatorUI() {
        super("Dispositivo IoT Simulado");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        // Panel superior - Nombre y frecuencia
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        deviceNameField = new JTextField("Sensor_01");
        dataRateField = new JTextField("10");

        topPanel.add(new JLabel("Nombre del dispositivo:"));
        topPanel.add(deviceNameField);
        topPanel.add(new JLabel("Datos por minuto:"));
        topPanel.add(dataRateField);

        // Panel centro - Áreas de texto
        JTabbedPane tabbedPane = new JTabbedPane();

        monitoringArea = new JTextArea();
        monitoringArea.setEditable(false);
        JScrollPane scrollMonitoring = new JScrollPane(monitoringArea);
        tabbedPane.addTab("Datos Monitoreo", scrollMonitoring);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollStatus = new JScrollPane(statusArea);
        tabbedPane.addTab("Datos Status", scrollStatus);

        // Panel inferior - Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton runButton = new JButton("Run");
        JButton stopButton = new JButton("Stop");


        runButton.addActionListener(e -> DeviceSimulator.getInstance().start(
                deviceNameField.getText(),
                dataRateField.getText(),
                this::appendMonitoring,
                this::appendStatus
        ));

        stopButton.addActionListener(e -> DeviceSimulator.getInstance().stop(this::appendStatus, deviceNameField.getText()));

        buttonPanel.add(runButton);
        buttonPanel.add(stopButton);

        // Agregar al JFrame
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void appendMonitoring(SimulatedDataDto data) {
        SwingUtilities.invokeLater(() -> {
            monitoringArea.append(data.nameDevice() + "  [" + data.dateOfRead() + "] " +  "temperatura: " + data.temperature() + "°C\n");
            monitoringArea.setCaretPosition(monitoringArea.getDocument().getLength());
        });
    }

    public void appendStatus(SimulatedStatusDto status) {
        SwingUtilities.invokeLater(() -> {
            statusArea.append("[" + new Date() + "] " + status.nameDevice() + " - " + status.status() +
                    " | Batería: " + status.battery() + "% | Temp: " + status.deviceTemperature() + "°C\n");
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        });
    }
}