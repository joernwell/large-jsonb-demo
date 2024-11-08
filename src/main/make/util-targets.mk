PORT = 8080
HOST = localhost

CHECK_NVD_KEY := $(shell grep -c "<id>nvd</id>" ~/.m2/settings.xml || echo 0)

# ================================
# Help: Print all .PHONY targets which have an inline comment (## comment)
# ================================

.PHONY: help ## Print a list ov available commands
help:
	@echo "Available commands:"
	@grep -E '^\.PHONY: [A-Za-z_-]+.*## ' $(MAKEFILE_LIST)  | awk 'BEGIN {FS = ": |## "}; {printf "  %-15s %s\n", $$2, $$3}'

# ================================
# Utility targets for Makefile
# ================================


.PHONY: spring-boot-start
spring-boot-start:
	@mvn clean spring-boot:run

.PHONY: spring-boot-stop
spring-boot-stop:
	@echo "Stopping Spring Boot application..."
	@curl -X POST http://$(HOST):$(PORT)/actuator/shutdown || echo "Application is not running or shutdown endpoint is disabled." || true
	@echo ""

.PHONY: docker-up
docker-up:
	@docker-compose up -d || true

.PHONY: docker-down
docker-down:
	@docker-compose down || true
	
.PHONY: mvn-release 
mvn-release:
	@mvn release:prepare -DpushChanges=false
	@$(MAKE) check-version
	@echo ""
	@echo "Last 3 commits in the GIT history:"
	@echo ""
	@git log -n 3 --oneline
	@echo ""
	@echo "Please verify the success of the release process."
	@echo "If everything is correct, execute the following commands to push the changes manually:"
	@for remote in $$(git remote); do \
		echo "    git push $$remote --tags"; \
		echo "    git push $$remote"; \
	done
	@echo "    mvn release:clean"
	@echo ""
	@echo "If there was an issue and you need to rollback the release, run the following command:"
	@tag_name=$$(grep "^scm.tag=" release.properties | cut -d'=' -f2) && echo "    git tag -d $$tag_name"
	@echo "    git reset --hard HEAD~2"
	@echo "    mvn release:clean"
	@echo "This will revert the version changes and delete any tags created during the release process."
	@echo ""
	
.PHONY: check-version
check-version:
	@tag_name=$$(grep "^scm.tag=" release.properties | cut -d'=' -f2); \
	app_version=$$(grep "^info.app.version=" src/main/resources/application.properties | cut -d'=' -f2); \
	if [ "$$tag_name" = "$$app_version" ]; then \
		echo "Version check passed: info.app.version ($$app_version) matches tag_name ($$tag_name)"; \
	else \
		echo "Version check failed: info.app.version ($$app_version) in src/main/resources/application.properties does not match tag_name ($$tag_name)"; \
		echo "Revert the mvn release process manually and update the info.app.version property:"; \
		tag_name=$$(grep "^scm.tag=" release.properties | cut -d'=' -f2); \
		echo "    git tag -d $$tag_name"; \
		echo "    git reset --hard HEAD~2"; \
		echo "    mvn release:clean"; \
		echo "This will revert the version changes and delete any tags created during the release process."; \
		exit 1; \
	fi
	
.PHONY: check_nvd_key
check-nvd-key:
	@CHECK_NVD_KEY=$$(grep -c "<id>nvd</id>" ~/.m2/settings.xml); \
	if [ $$CHECK_NVD_KEY -eq 0 ]; then echo "=== NVD API key is missing in settings.xml ==="; \
		echo "Security check: This plugin scans your project’s dependencies for known vulnerabilities, "; \
		echo "helping you identify and mitigate potential security risks."; \
		echo "Request an API key on https://nvd.nist.gov/developers/request-an-api-key"; \
		echo "It's for free!"; \
		echo "Add the NVD API key to the settings.xml as follows:"; \
		echo "<server>"; \
		echo "  <id>nvd</id>"; \
		echo "  <password>your-api-key</password>"; \
		echo "</server>"; \
		exit 1; \
	fi;

.PHONY: wait-for-application
wait-for-application:
	@echo "Waiting for application to start on port ${HOST}:${PORT}..."
	@while ! nc -z ${HOST} ${PORT}; do \
		echo "Port ${PORT} is not open. Waiting..."; \
		sleep 1; \
	done
	@echo "Application is running on port ${PORT}. Proceeding with JSON generation..."

