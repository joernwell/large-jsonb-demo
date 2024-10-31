# Define the paths and commands
SPRING_BOOT_APP = mvn spring-boot:run
PORT = 8080
HOST = localhost

# Default target
.PHONY: start
start: docker-up spring-boot

# Start Docker Compose
.PHONY: docker-up
docker-up:
	docker-compose up -d || true

# Start Spring Boot application
.PHONY: spring-boot
spring-boot:
	$(SPRING_BOOT_APP)

# Stop all services
.PHONY: stop
stop: spring-boot-stop docker-down

# Stop the Spring Boot application using the /actuator/shutdown endpoint
.PHONY: spring-boot-stop
spring-boot-stop:
	@echo "Stopping Spring Boot application..."
	@curl -X POST http://$(HOST):$(PORT)/actuator/shutdown || echo "Application is not running or shutdown endpoint is disabled."

# Stop Docker Compose services
.PHONY: docker-down
docker-down:
	docker-compose down