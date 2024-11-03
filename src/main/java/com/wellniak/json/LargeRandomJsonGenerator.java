package com.wellniak.json;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the {@link LargeRandomJsonGenerator}.
 * <p>
 * This application generates large random JSON data for testing and development purposes. It uses Spring Boot to
 * bootstrap and run the application.
 * </p>
 *
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
public class LargeRandomJsonGenerator {

	/**
	 * Main method to launch the LargeRandomJsonGenerator application.
	 *
	 * @param args Command-line arguments for application startup (not currently supported).
	 */
	public static void main(String[] args) {
		SpringApplication.run(LargeRandomJsonGenerator.class, args);
	}
}
