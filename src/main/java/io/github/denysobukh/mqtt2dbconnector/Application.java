package io.github.denysobukh.mqtt2dbconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the MQTT to database connector as a Spring Boot application.
 */
@SpringBootApplication
public class Application {

	/**
	 * Boots the Spring application context and starts the MQTT connector service.
	 *
	 * @param args command-line arguments passed to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
