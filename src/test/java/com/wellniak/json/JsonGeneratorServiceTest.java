package com.wellniak.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JsonGeneratorServiceTest {

    @Mock
    private JsonTestRepository jsonTestRepository;

    @InjectMocks
    private JsonGeneratorService jsonGeneratorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void generateAndSaveJson_createsCorrectNumberOfRecords() {
        int count = 10;

        // Call the method with a specific count
        jsonGeneratorService.generateAndSaveJson(count);

        // Verify that saveAll was called once and the list size passed matches the count
        verify(jsonTestRepository, times(1)).saveAll(argThat((List<JsonTest> list) -> list.size() == count));
    }

    @Test
    void generateRandomJson_createsValidJsonStructure() {
        // Generate JSON with known depth
        ObjectNode jsonData = jsonGeneratorService.generateRandomJson(1, 2);

        // Assertions to verify JSON structure
        assertNotNull(jsonData);
        assertTrue(jsonData.has("attribute_1_1"));
        assertTrue(jsonData.has("attribute_1_2"));
        assertTrue(jsonData.has("attribute_1_3"));
    }

    @Test
    void getRandomString_generatesStringWithExpectedLength() {
        int minLength = 10;
        int maxLength = 20;

        // Call the method and check the result
        String randomString = jsonGeneratorService.getRandomString(minLength, maxLength);
        
        // Assertions to check string length
        assertNotNull(randomString);
        assertTrue(randomString.length() >= minLength && randomString.length() <= maxLength);
    }

    @Test
    void generateRandomJson_createsCorrectAttributesBasedOnType() {
        ObjectNode jsonData = jsonGeneratorService.generateRandomJson(1, 1);

        // Check types of the specific attributes based on ATTRIBUTE_TYPES
        assertTrue(jsonData.get("attribute_1_1").isTextual(), "attribute_1_1 should be a String");
        assertTrue(jsonData.get("attribute_1_2").isTextual(), "attribute_1_2 should be a Numeric String");
        assertTrue(jsonData.get("attribute_1_7").isBoolean(), "attribute_1_7 should be a Boolean");
    }
}