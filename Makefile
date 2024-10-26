# Define the paths and commands
SPRING_BOOT_APP = mvn spring-boot:run

# Default target
.PHONY: start
start: docker-up spring-boot

# Start Docker Compose
.PHONY: docker-up
docker-up:
	docker-compose up -d || true
	@echo "Waiting for Docker services to start..."
	@sleep 4  # Adjust sleep if more time is needed for containers to initialize

# Start Spring Boot application
.PHONY: spring-boot
spring-boot:
	$(SPRING_BOOT_APP)

# Stop all services
.PHONY: stop
stop: docker-down

# Stop Docker Compose services
.PHONY: docker-down
docker-down:
	docker-compose down

