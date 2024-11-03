package com.wellniak.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for generating and persisting random JSON data in the database.
 * <p>
 * This service creates random JSON structures with nested attributes, based on predefined attribute types. The JSON
 * data is stored in the database as records of {@link JsonTest} entities. The service supports generation of large
 * batches and uses transaction management to optimize database performance.
 */
@Service
@Transactional
public class JsonGeneratorService {
	private static final int NUM_ATTRIBUTES = 20;
	private static final Map<String, Integer> ATTRIBUTE_TYPES = new HashMap<>();
	private static final Random RANDOM = new Random();

	@Autowired
	private JsonTestRepository jsonTestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@PersistenceContext
	private EntityManager entityManager;

	static {
		// Fixed type assignments for the 50 attributes
		ATTRIBUTE_TYPES.put("attribute_1", 0); // String
		ATTRIBUTE_TYPES.put("attribute_2", 1); // Numeric String
		ATTRIBUTE_TYPES.put("attribute_3", 0); // String
		ATTRIBUTE_TYPES.put("attribute_4", 3); // Nested JSON
		ATTRIBUTE_TYPES.put("attribute_5", 0); // String
		ATTRIBUTE_TYPES.put("attribute_6", 1); // Numeric String
		ATTRIBUTE_TYPES.put("attribute_7", 2); // Boolean
		ATTRIBUTE_TYPES.put("attribute_8", 0); // String
		ATTRIBUTE_TYPES.put("attribute_9", 3); // Nested JSON
		ATTRIBUTE_TYPES.put("attribute_10", 1); // Integer

		// Additional 40 randomly assigned attributes
		for (var i = 11; i <= NUM_ATTRIBUTES; i++) {
			var valueType = i % 4; // Cyclical assignment: String, Integer, Boolean, Nested JSON
			ATTRIBUTE_TYPES.put("attribute_" + i, valueType);
		}
	}

	/**
	 * Generates and saves a specified number of random JSON entries in the database.
	 * <p>
	 * Each JSON entry is created with randomly assigned attributes based on pre-defined types (String, Numeric String,
	 * Boolean, Nested JSON). The JSON data is converted to a string and stored in {@link JsonTest} entities.
	 *
	 * @param count the number of JSON entries to generate and save
	 * @throws RuntimeException if an error occurs during JSON conversion
	 */
	public void generateAndSaveJson(int count) {
		List<JsonTest> batchList = new ArrayList<>(count);

		for (var i = 0; i < count; i++) {
			var jsonData = generateRandomJson(1, 4);

			// Use a StringWriter and JsonGenerator for streaming
			String jsonString;
			try (var writer = new StringWriter(); var generator = objectMapper.getFactory().createGenerator(writer)) {

				objectMapper.writeValue(generator, jsonData); // Stream JSON to writer
				jsonString = writer.toString();

			} catch (Exception e) {
				throw new RuntimeException("Error converting JSON to string", e);
			}

			var jsonTest = new JsonTest();
			jsonTest.setData(jsonString);
			batchList.add(jsonTest);
		}

		if (!batchList.isEmpty()) {
			jsonTestRepository.saveAll(batchList);
			jsonTestRepository.flush();
			batchList.clear();
			entityManager.clear();
		}
	}

	/**
	 * Retrieves the total count of records in the {@link JsonTest} table.
	 *
	 * @return the total number of {@link JsonTest} records in the database
	 */
	public long getTotalRecordCount() {
		return jsonTestRepository.count();
	}

	/**
	 * Recursively generates a random JSON object with nested attributes up to a specified level.
	 * <p>
	 * The JSON structure is built with attributes that can be strings, numbers, booleans, or nested JSON objects.
	 * Attribute types are determined by a predefined mapping.
	 *
	 * @param currentLevel the current nesting level of the JSON structure
	 * @param maxLevel the maximum depth of the nested JSON
	 * @return an {@link ObjectNode} representing the generated JSON object
	 */
	private ObjectNode generateRandomJson(int currentLevel, int maxLevel) {
		var node = objectMapper.createObjectNode();
		if (currentLevel > maxLevel) {
			return node;
		}

		for (var i = 0; i < NUM_ATTRIBUTES; i++) {
			var attributeName = "attribute_" + currentLevel + "_" + (i + 1);
			int valueType = ATTRIBUTE_TYPES.get("attribute_" + (i + 1));

			if (i == 1) {
				node.put(attributeName, "value_%d".formatted(i));
			}

			switch (valueType) {
				case 0:
					node.put(attributeName, getRandomString(50, 150));
					break;
				case 1:
					node.put(attributeName, String.valueOf(RANDOM.nextInt(100000)));
					break;
				case 2:
					node.put(attributeName, RANDOM.nextBoolean());
					break;
				case 3:
					if (currentLevel < maxLevel) {
						node.set(attributeName, generateRandomJson(currentLevel + 1, maxLevel));
					}
					break;
			}
		}

		return node;
	}

	/**
	 * Generates a random alphanumeric string of specified length.
	 *
	 * @param minLength the minimum length of the generated string
	 * @param maxLength the maximum length of the generated string
	 * @return a random alphanumeric string with a length between minLength and maxLength
	 */
	private String getRandomString(int minLength, int maxLength) {
		var length = minLength + RANDOM.nextInt(maxLength - minLength + 1);
		var sb = new StringBuilder(length);
		for (var i = 0; i < length; i++) {
			sb.append((char) ('a' + RANDOM.nextInt(26)));
		}
		return sb.toString();
	}
}
