package no.fdk.skosmosstore.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.fdk.skosmosstore.utils.TestQuery;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.profiles.active=test")
@Tag("integration")
public class IntegrationTest {
    ObjectMapper mapper = new ObjectMapper();

    private String countQuery(String graph) {
        return "SELECT (COUNT(DISTINCT ?subj) AS ?count)\n" +
                "FROM <"+ graph + ">\n" +
                "WHERE { ?subj ?pred ?obj . }\n";
    }

    private Integer getCountFromSelectResponse(String response) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(response);
        return jsonNode.get("results").get("bindings").get(0).get("count").get("value").asInt();
    }

    @Test
    void countCurrentLOSTriples() throws Exception {
        String response = TestQuery.sendQuery(countQuery("https://id.norge.no/los"));
        Integer result = getCountFromSelectResponse(response);
        Assertions.assertEquals(637, result);
    }

    @Test
    void countArchivedBetaLOSTriples() throws Exception {
        String response = TestQuery.sendQuery(countQuery("https://id.norge.no/los-beta-archived"));
        Integer result = getCountFromSelectResponse(response);
        Assertions.assertEquals(437, result);
    }

}
