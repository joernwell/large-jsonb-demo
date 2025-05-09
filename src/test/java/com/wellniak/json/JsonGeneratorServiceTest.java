package com.wellniak.json;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JsonGeneratorServiceTest {

    @Mock
    private JsonTestRepository jsonTestRepository;

    @Mock
    private JsonTestDataService jsonTestDataService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonGeneratorService jsonGeneratorService;


    public JsonGeneratorServiceTest() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * Test case: Validate that generateAndSaveJson method generates and saves the correct number of JSON objects.
     */
    @Test
    public void testGenerateAndSaveJson_ValidCount_SavesCorrectNumber() {
        int count = 5;

        doNothing().when(jsonTestDataService).saveBatch(anyList());

        jsonGeneratorService.generateAndSaveJson(count);

        verify(jsonTestDataService, times(1)).saveBatch(argThat(batch -> batch.size() == count));
    }


    /**
     * Test case: Validate that no saving occurs when count is zero.
     */
    @Test
    public void testGenerateAndSaveJson_ZeroCount_NoSave() {
        int count = 0;

        jsonGeneratorService.generateAndSaveJson(count);

        verify(jsonTestDataService, never()).saveBatch(anyList());
    }


    /**
     * Test case: Validate behavior when ObjectMapper throws an exception during JSON serialization.
     */
    @Test
    public void testGenerateAndSaveJson_ObjectMapperError_ThrowsRuntimeException() {
        int count = 1;

        when(objectMapper.getFactory()).thenThrow(new RuntimeException("ObjectMapper error"));

        assertThrows(RuntimeException.class, () -> jsonGeneratorService.generateAndSaveJson(count));
    }


    /**
     * Test case: Validate behavior when object creation leads to empty JSON string.
     */
    @Test
    public void testGenerateAndSaveJson_EmptyJson_NoSave() {
        int count = 1;

        when(objectMapper.createObjectNode()).thenReturn(objectMapper.createObjectNode());
        doNothing().when(jsonTestDataService).saveBatch(any());

        jsonGeneratorService.generateAndSaveJson(count);

        verify(jsonTestDataService, never()).saveBatch(argThat(List::isEmpty));
    }


    /**
     * Test case: Validate correct number of records are retrieved from the repository.
     */
    @Test
    public void testGetTotalRecordCount_CorrectCountReturned() {
        long expectedCount = 100L;

        when(jsonTestRepository.count()).thenReturn(expectedCount);

        long actualCount = jsonGeneratorService.getTotalRecordCount();

        verify(jsonTestRepository, times(1)).count();
        assert (actualCount == expectedCount);
    }
}