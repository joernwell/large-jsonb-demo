package com.wellniak.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonController {

    private static final Logger logger = LoggerFactory.getLogger(JsonGeneratorService.class);

    private final JsonGeneratorService jsonGeneratorService;

    @Autowired
    public JsonController(JsonGeneratorService jsonGeneratorService) {
        this.jsonGeneratorService = jsonGeneratorService;
    }

    @GetMapping("/generate-json")
    public String generateJson(
            @RequestParam(value = "numRecords", defaultValue = "10000") int numRecords,
            @RequestParam(value = "bulkSize", defaultValue = "100") int bulkSize) {

        logger.info("Start generating JSON with numRecords={} and bulkSize={}", numRecords, bulkSize);

        // Start multiple threads to process batches in parallel
        for (int i = 0; i < numRecords / bulkSize; i++) {
            jsonGeneratorService.generateAndSaveJson(bulkSize);
            logger.info("{} entries written", bulkSize * (i + 1));
        }

        return "JSON was generated and saved to the database.";
    }
}