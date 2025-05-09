package co.scastillos.apiDeviceIoT.controller;

import co.scastillos.apiDeviceIoT.config.DynamicRabbitConsumer;
import co.scastillos.apiDeviceIoT.dto.DeviceDataMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;

    private final DynamicRabbitConsumer dynamicRabbitConsumer;

    public TestController(SimpMessagingTemplate messagingTemplate, DynamicRabbitConsumer dynamicRabbitConsumer) {
        this.messagingTemplate = messagingTemplate;
        this.dynamicRabbitConsumer = dynamicRabbitConsumer;
    }

    @GetMapping("/test")
    public void test() {
        DeviceDataMessage msg = DeviceDataMessage.builder()
                .deviceId("TEST")
                .temperature(99.9)
                .timestamp(System.currentTimeMillis())
                .build();

        messagingTemplate.convertAndSend("/topic/device/data", msg);
    }


    @GetMapping("/subscribe/{device}")
    public String subscribe(@PathVariable String device) {
        System.out.println("📞 Suscribiendo a dispositivo: " + device);
        dynamicRabbitConsumer.subscribeToDeviceQueues(device);
        return "Intentando suscribirse a dispositivo: " + device;
    }

}
