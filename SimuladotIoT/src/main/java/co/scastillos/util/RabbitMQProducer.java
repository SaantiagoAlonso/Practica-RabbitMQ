package co.scastillos.util;

import co.scastillos.dto.SimulatedDataDto;
import co.scastillos.dto.SimulatedStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {

    private final String queueData;
    private final String queueStatus;

    private final Channel channel;
    private final ObjectMapper mapper;


    public RabbitMQProducer(String deviceName) throws IOException, TimeoutException {
        this.mapper = new ObjectMapper();

        this.queueData = "deviceIoT_" + deviceName + "_data";
        this.queueStatus = "deviceIoT_" + deviceName + "_status";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("santiago");
        factory.setPassword("santiago123");

        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        channel.queueDeclare(queueData, false, false, false, null);
        channel.queueDeclare(queueStatus, false, false, false, null);

        System.out.println("🔌 Conectado a RabbitMQ. Colas creadas:");
        System.out.println("   → Datos: " + queueData);
        System.out.println("   → Estado: " + queueStatus);
    }

    public void sendData(SimulatedDataDto dataDto) {
        try {
            String json = mapper.writeValueAsString(dataDto);
            channel.basicPublish("", queueData, null, json.getBytes());
            System.out.println("📊 [DATA] Enviado a '" + queueData + "': " + json);
        } catch (JsonProcessingException e) {
            System.err.println("❌ Error al serializar SimulatedDataDto: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Error al enviar mensaje a RabbitMQ: " + e.getMessage());
        }
    }

    public void sendStatus(SimulatedStatusDto statusDto) {
        try {
            String json = mapper.writeValueAsString(statusDto);
            channel.basicPublish("", queueStatus, null, json.getBytes());
            System.out.println("🔋 [STATUS] Enviado a '" + queueStatus + "': " + json);
        } catch (JsonProcessingException e) {
            System.err.println("❌ Error al serializar SimulatedStatusDto: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Error al enviar mensaje a RabbitMQ: " + e.getMessage());
        }
    }


}
