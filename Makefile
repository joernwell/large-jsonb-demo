include src/main/make/util-targets.mk

.PHONY: start ## Start Docker and Spring Boot
.PHONY: stop ## Stop Spring Boot and Docker services
.PHONY: clean ## Stop Docker and remove volumes
.PHONY: it ## Perform integration tests
.PHONY: generate ## Generate 2,000 JSON entries in the database
.PHONY: release ## Prepares and performs a release without automatically pushing changes to Git
.PHONY: security ## Performs security check. Make sure that the NVD API key is stored in the settings.xml

# ================================
# Targets
# ================================

start: 
	$(MAKE) docker-up 
	$(MAKE) spring-boot-start

stop: 
	@$(MAKE) spring-boot-stop
	@$(MAKE) docker-down

clean: 
	@$(MAKE) spring-boot-stop
	@echo "Removing Docker volumes..."
	@docker-compose down -v

it: 
	@mvn clean verify

generate: 
	@$(MAKE) wait-for-application
	@curl "http://${HOST}:${PORT}/generate-json?numRecords=2000&bulkSize=500"
	@echo ""
	
release:
	@$(MAKE) mvn-release

security:
	@$(MAKE) check-nvd-key
	@echo "NVD API key is already configured in settings.xml."
	@export MAVEN_OPTS="--add-modules jdk.incubator.vector" && mvn verify -P security -Dorg.apache.lucene.store.MMapDirectory.enableMemorySegments=false;
