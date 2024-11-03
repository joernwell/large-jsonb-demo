package com.wellniak.json;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Base class for integration tests that require a PostgreSQL database.
 * <p>
 * This class uses Testcontainers to start and stop a PostgreSQL container for testing purposes. It sets up the
 * container with a dummy database and configures Spring properties dynamically to connect to it.
 * <p>
 * Other integration test classes can extend this class to reuse the container setup and configuration.
 */
abstract class BaseIntegrationTest {

	@SuppressWarnings("resource")
	@Container
	static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("postgres").withUsername("dummy").withPassword("dummy");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);
	}

	@BeforeAll
	public static void startContainer() {
		postgresContainer.start();
	}

	@AfterAll
	public static void stopContainer() {
		postgresContainer.stop();
	}

}
