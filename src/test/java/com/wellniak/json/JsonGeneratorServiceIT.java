package com.wellniak.json;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "management.endpoint.shutdown.enabled=true",
        "management.endpoints.web.exposure.include=shutdown"
})
@RunWith(SpringRunner.class)
class JsonGeneratorServiceIT extends BaseIntegrationTest {

    @Autowired
    private JsonGeneratorService jsonGeneratorService;

    @Autowired
    private JsonTestRepository jsonTestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGenerateAndSaveJsonRecords() throws Exception {
        // given
        int expectedCount = 5;
        jsonTestRepository.deleteAll();

        // when
        jsonGeneratorService.generateAndSaveJson(expectedCount);

        // then
        long actualCount = jsonGeneratorService.getTotalRecordCount();
        assertThat(actualCount).isEqualTo(expectedCount);

        // verify json structure
        JsonTest firstRecord = jsonTestRepository.findAll().get(0);
        ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(firstRecord.getData());

        assertThat(jsonNode.has("attribute_1_1")).isTrue();
        assertThat(jsonNode.has("attribute_1_2")).isTrue();
        assertThat(jsonNode.get("attribute_1_2").asText()).startsWith("value_1");
        assertThat(jsonNode.has("attribute_1_4")).isTrue();
        assertThat(jsonNode.get("attribute_1_4").isObject()).isTrue();
    }
}