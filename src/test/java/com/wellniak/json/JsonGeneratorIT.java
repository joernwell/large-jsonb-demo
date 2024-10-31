package com.wellniak.json;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.endpoint.shutdown.enabled=true",
		"management.endpoints.web.exposure.include=shutdown" })
@RunWith(SpringRunner.class)
class JsonGeneratorIT extends BaseIntegrationTest {

	@Autowired
	private JsonTestRepository jsonTestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@LocalServerPort
	private Integer port;

	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	void shouldGenerateEntriesInBatches() throws JsonMappingException, JsonProcessingException {
		RestAssured.port = port;

		given().contentType(ContentType.JSON).param("numRecords", 10).param("bulkSize", 3).when().get("/generate-json")
				.then().statusCode(200);

		assertThat(jsonTestRepository.count()).isEqualTo(10);

		JsonTest first = jsonTestRepository.findAll().stream().findFirst().orElse(null);
		String attribute22 = getNestedAttribute(first);
		List<JsonTest> jsonTestsForAttr22 = jsonTestRepository.findByAttributeNestedValue(attribute22);
		assertThat(jsonTestsForAttr22).size().isNotZero();
	}

	String getNestedAttribute(JsonTest jsonTest) throws JsonMappingException, JsonProcessingException {
		JsonNode rootNode = objectMapper.readTree(jsonTest.getData());
		JsonNode attribute2_2Node = rootNode.path("attribute_1_4").path("attribute_2_2");
		if (attribute2_2Node.isMissingNode()) {
			return null; // Or handle missing attribute as needed
		}
		return attribute2_2Node.asText(); // Get the value as text
	}
}