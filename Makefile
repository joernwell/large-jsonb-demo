# Help target to list all commands
.PHONY: help
help:
	@echo "Available commands:"
	@grep -E '^#[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {sub(/^#/, "", $$1); printf "  %-15s %s\n", $$1, $$2}'

# Define targets with comments for help
#start: docker-up spring-boot ## Start Docker and Spring Boot
#stop: spring-boot-stop docker-down ## Stop Spring Boot and Docker services
#reset: docker-down ## Stop Docker and remove volumes

# Define the paths and commands
SPRING_BOOT_APP = mvn spring-boot:run
PORT = 8080
HOST = localhost

# Start target
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
	
# Reset Docker: stop containers and remove volumes
.PHONY: reset
reset: spring-boot-stop
	@echo "Removing Docker volumes..."
	docker-compose down -v

