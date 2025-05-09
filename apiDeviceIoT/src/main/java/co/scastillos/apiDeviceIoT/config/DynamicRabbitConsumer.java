package co.scastillos.apiDeviceIoT.config;

import co.scastillos.apiDeviceIoT.dto.DeviceDataMessage;
import co.scastillos.apiDeviceIoT.dto.DeviceStatusMessage;
import co.scastillos.apiDeviceIoT.dto.SimulatedDataDto;
import co.scastillos.apiDeviceIoT.dto.SimulatedStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicRabbitConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<String> devicesSubscribed = ConcurrentHashMap.newKeySet();
    private final ConnectionFactory connectionFactory;
    private final SimpMessagingTemplate messagingTemplate;

    public DynamicRabbitConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("santiago");
        connectionFactory.setPassword("santiago123");
    }

    public void subscribeToDeviceQueues(String deviceName) {

        if (devicesSubscribed.contains(deviceName)) {
            System.out.println("🟡 Ya suscrito a colas de " + deviceName);
            return;
        }

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            String dataQueue = "deviceIoT_" + deviceName + "_data";
            String statusQueue = "deviceIoT_" + deviceName + "_status";

            channel.basicConsume(dataQueue, true, (consumerTag, message) -> {
                String body = new String(message.getBody());
                System.out.println("📩 Mensaje crudo recibido: " + body);

                try {

                    SimulatedDataDto data = objectMapper.readValue(body, SimulatedDataDto.class);

                    DeviceDataMessage msg = DeviceDataMessage.builder()
                            .deviceId(data.nameDevice())
                            .temperature(data.temperature())
                            .timestamp(data.dateOfRead().getTime())
                            .build();

                    System.out.println(msg);

                    messagingTemplate.convertAndSend("/topic/device/data", msg);

                    System.out.println("Enviando mensaje a WebSocket: " + msg);

                } catch (Exception e) {
                    System.err.println("❌ Error procesando mensaje de datos: " + e.getMessage());
                }
            }, consumerTag -> {});


            channel.basicConsume(statusQueue, true, (consumerTag, message) -> {
                String body = new String(message.getBody());
                try {
                    SimulatedStatusDto status = objectMapper.readValue(body, SimulatedStatusDto.class);

                    System.out.println("Mensaje crudo recibido: " + body);

                    DeviceStatusMessage msg = DeviceStatusMessage.builder()
                            .deviceId(status.nameDevice())
                            .status(status.status())
                            .battery(status.battery())
                            .temperature(status.deviceTemperature())
                            .timestamp(System.currentTimeMillis())
                            .build();

                    messagingTemplate.convertAndSend("/topic/device/status", msg);

                } catch (Exception e) {
                    System.err.println("❌ Error procesando mensaje de estado: " + e.getMessage());
                }
            }, consumerTag -> {});

            devicesSubscribed.add(deviceName);

        } catch (Exception e) {
            System.err.println("❌ Error al suscribirse a colas de " + deviceName + ": " + e.getMessage());
        }
    }
}


