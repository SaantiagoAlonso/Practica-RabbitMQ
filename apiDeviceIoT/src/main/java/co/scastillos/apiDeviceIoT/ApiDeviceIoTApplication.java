package co.scastillos.apiDeviceIoT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiDeviceIoTApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDeviceIoTApplication.class, args);
	}

}
