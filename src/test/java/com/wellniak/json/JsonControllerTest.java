package com.wellniak.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JsonController.class)
public class JsonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // MockBean is registered as a Spring Bean in the application context
    private JsonGeneratorService jsonGeneratorService;

    @Test
    void generateJson_withDefaultParameters() throws Exception {
        // Perform GET request without query parameters to use defaults
        mockMvc.perform(get("/generate-json"))
                .andExpect(status().isOk())
                .andExpect(content().string("JSON was generated and saved to the database."));

        // Verify generateAndSaveJson was called 100 times (NUM_RECORDS / BULK_SIZE)
        verify(jsonGeneratorService, times(100)).generateAndSaveJson(100);
    }

    @Test
    void generateJson_withCustomParameters() throws Exception {
        int numRecords = 5000;
        int bulkSize = 200;

        // Perform GET request with custom query parameters
        mockMvc.perform(get("/generate-json")
                .param("numRecords", String.valueOf(numRecords))
                .param("bulkSize", String.valueOf(bulkSize)))
                .andExpect(status().isOk())
                .andExpect(content().string("JSON was generated and saved to the database."));

        // Verify generateAndSaveJson was called 25 times (5000 / 200)
        verify(jsonGeneratorService, times(25)).generateAndSaveJson(bulkSize);
    }
}