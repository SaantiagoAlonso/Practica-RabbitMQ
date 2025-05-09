package co.scastillos.util;

import co.scastillos.dto.SimulatedDataDto;
import co.scastillos.dto.SimulatedStatusDto;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DeviceSimulator {

    private static final DeviceSimulator INSTANCE = new DeviceSimulator();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledTask;
    private Random random = new Random();

    private RabbitMQProducer rabbitMQProducer;

    private DeviceSimulator() {}

    public static DeviceSimulator getInstance() {
        return INSTANCE;
    }


    public void start(String name, String rateStr, Consumer<SimulatedDataDto> onNewData, Consumer<SimulatedStatusDto> onStatusUpdate) {

        if (rabbitMQProducer == null) {
            try {
                rabbitMQProducer = new RabbitMQProducer(name);
            } catch (Exception e) {
                System.err.println("❌ No se pudo conectar a RabbitMQ");
                return;
            }
        }

        if (scheduledTask != null && !scheduledTask.isDone()) {
            onStatusUpdate.accept(SimulatedStatusDto.builder()
                    .nameDevice(name)
                    .battery(100)
                    .status("Simulación ya en ejecución")
                    .deviceTemperature(0.0)
                    .build());
            return;
        }

        int ratePerMinute;
        try {
            ratePerMinute = Integer.parseInt(rateStr);
        } catch (NumberFormatException ex) {
            onStatusUpdate.accept(SimulatedStatusDto.builder()
                    .nameDevice(name)
                    .battery(100)
                    .status("Frecuencia inválida")
                    .deviceTemperature(0.0)
                    .build());
            return;
        }

        long period = 60_000 / ratePerMinute;

        // Estado inicial al iniciar
        onStatusUpdate.accept(SimulatedStatusDto.builder()
                .nameDevice(name)
                .battery(100)
                .status("Iniciando simulación...")
                .deviceTemperature(0.0)
                .build());

        scheduledTask = scheduler.scheduleAtFixedRate(() -> {

            double value = Math.round((20 + random.nextDouble() * 10) * 10.0) / 10.0; // entre 20 y 30 °C
            Date timestamp = new Date(); // Fecha actual

            // Generamos un dato de temperatura
            SimulatedDataDto dataDto = SimulatedDataDto.builder()
                    .nameDevice(name)
                    .temperature(value)
                    .dateOfRead(timestamp)
                    .build();

            // Enviamos el dato a la UI
            onNewData.accept(dataDto);
            rabbitMQProducer.sendData(dataDto);

            SimulatedStatusDto statusDto = SimulatedStatusDto.builder()
                    .nameDevice(name)
                    .battery(95)
                    .status("En funcionamiento")
                    .deviceTemperature(value)
                    .build();

            onStatusUpdate.accept(statusDto);
            rabbitMQProducer.sendStatus(statusDto);

            rabbitMQProducer.sendStatus(statusDto);

        }, 0, period, TimeUnit.MILLISECONDS);
    }


    public void stop(Consumer<SimulatedStatusDto> onStatusUpdate, String name) {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(true);
            onStatusUpdate.accept(SimulatedStatusDto.builder()
                    .nameDevice(name)
                    .battery(95)
                    .status("Simulación detenida")
                    .deviceTemperature(0.0)
                    .build());
        } else {
            onStatusUpdate.accept(SimulatedStatusDto.builder()
                    .nameDevice(name)
                    .battery(100)
                    .status("No hay simulación activa")
                    .deviceTemperature(0.0)
                    .build());
        }
    }



}
