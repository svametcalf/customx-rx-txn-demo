package com.example.custom_rx_demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.time.Instant;

@SpringBootTest(properties = "logging.level.org.springframework.transaction=TRACE")
@Testcontainers
class CustomRxDemoApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16-alpine"))
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");

	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.r2dbc.url", () ->
				String.format("r2dbc:postgresql://%s:%d/%s",
						postgres.getHost(),
						postgres.getMappedPort(5432),
						postgres.getDatabaseName()));
		registry.add("spring.r2dbc.username", postgres::getUsername);
		registry.add("spring.r2dbc.password", postgres::getPassword);

		registry.add("spring.liquibase.url", () ->
				String.format("jdbc:postgresql://%s:%d/%s",
						postgres.getHost(),
						postgres.getMappedPort(5432),
						postgres.getDatabaseName()));
		registry.add("spring.liquibase.user", postgres::getUsername);
		registry.add("spring.liquibase.password", postgres::getPassword);
	}

	@Autowired
	CustomRxDemoApplication.SomeRecordService service;

	@Test
	void itSavesWithARegularMono() {
		service.save(new CustomRxDemoApplication.SomeRecord(1, "some-text-here", Instant.now()))
				.log()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();

		service.getAll()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}

	@Test
	@DisplayName("it works with a custom Rxtype")
	public void itWorksWithACustomRxtype() {
		service.saveWrapped(new CustomRxDemoApplication.SomeRecord(3, "some-other-text", Instant.now()))
				.asMono()
				.log()
				.as(StepVerifier::create)
				.expectNextCount( 1)
				.verifyComplete();

		service.getAll().as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}
}
