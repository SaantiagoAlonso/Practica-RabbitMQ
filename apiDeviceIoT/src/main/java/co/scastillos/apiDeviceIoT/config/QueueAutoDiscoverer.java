package co.scastillos.apiDeviceIoT.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Component
public class QueueAutoDiscoverer {

    @Value("${rabbitmq.api.url:http://localhost:15672/api/queues}")
    private String apiUrl;

    @Value("${rabbitmq.api.username}")
    private String username;

    @Value("${rabbitmq.api.password}")
    private String password;

    private final DynamicRabbitConsumer consumer;

    public QueueAutoDiscoverer(DynamicRabbitConsumer consumer) {
        this.consumer = consumer;
    }

    @Scheduled(fixedDelay = 10000) // Cada 10 segundos
    public void scanForNewQueues() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, request,
                    new ParameterizedTypeReference<>() {}
            );

            for (Map<String, Object> queue : response.getBody()) {
                String queueName = (String) queue.get("name");
                if (queueName.startsWith("deviceIoT_") && (queueName.endsWith("_data") || queueName.endsWith("_status"))) {
                    String deviceName = queueName.substring("deviceIoT_".length(), queueName.lastIndexOf("_"));
                    consumer.subscribeToDeviceQueues(deviceName);
                }
            }

        } catch (Exception e) {
            System.err.println("Error escaneando colas: " + e.getMessage());
        }
    }
}


