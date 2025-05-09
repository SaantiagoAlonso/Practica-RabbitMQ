package co.scastillos;

import co.scastillos.ui.DeviceSimulatorUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeviceSimulatorUI::new);
    }
}