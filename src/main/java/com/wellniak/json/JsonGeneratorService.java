package com.wellniak.json;

import com.fasterxml.jackson.core.JsonGenerator;
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

@Service
public class JsonGeneratorService {
	private static final int NUM_ATTRIBUTES = 20;
	private static final Map<String, Integer> ATTRIBUTE_TYPES = new HashMap<>();
	private static final Random RANDOM = new Random();

	private final JsonTestRepository jsonTestRepository;

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
		for (int i = 11; i <= NUM_ATTRIBUTES; i++) {
			int valueType = i % 4; // Cyclical assignment: String, Integer, Boolean, Nested JSON
			ATTRIBUTE_TYPES.put("attribute_" + i, valueType);
		}
	}

	@Autowired
	public JsonGeneratorService(JsonTestRepository jsonTestRepository) {
		this.jsonTestRepository = jsonTestRepository;
	}

	@Transactional
	public void generateAndSaveJson(int count) {
		List<JsonTest> batchList = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			ObjectNode jsonData = generateRandomJson(1, 4);

			// Use a StringWriter and JsonGenerator for streaming
			String jsonString;
			try (StringWriter writer = new StringWriter();
					JsonGenerator generator = objectMapper.getFactory().createGenerator(writer)) {

				objectMapper.writeValue(generator, jsonData); // Stream JSON to writer
				jsonString = writer.toString();

			} catch (Exception e) {
				throw new RuntimeException("Error converting JSON to string", e);
			}

			JsonTest jsonTest = new JsonTest();
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

	ObjectNode generateRandomJson(int currentLevel, int maxLevel) {
		ObjectNode node = objectMapper.createObjectNode();
		if (currentLevel > maxLevel) {
			return node;
		}

		for (int i = 0; i < NUM_ATTRIBUTES; i++) {
			String attributeName = "attribute_" + currentLevel + "_" + (i + 1);
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

	String getRandomString(int minLength, int maxLength) {
		int length = minLength + RANDOM.nextInt(maxLength - minLength + 1);
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append((char) ('a' + RANDOM.nextInt(26)));
		}
		return sb.toString();
	}
}
