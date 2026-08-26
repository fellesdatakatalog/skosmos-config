package no.fdk.skosmosstore.integration;

import com.jayway.jsonpath.JsonPath;
import no.fdk.skosmosstore.utils.TestQuery;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "spring.profiles.active=test")
@Tag("integration")
public class IntegrationTest {

    private String countQuery(String graph) {
        return "SELECT (COUNT(DISTINCT ?subj) AS ?count)\n" +
                "FROM <"+ graph + ">\n" +
                "WHERE { ?subj ?pred ?obj . }\n";
    }

    private Integer getCountFromSelectResponse(String response) {
        Object value = JsonPath.read(response, "$.results.bindings[0].count.value");
        return Integer.valueOf(value.toString());
    }

    @Test
    void countCurrentLOSTriples() throws Exception {
        String response = TestQuery.sendQuery(countQuery("https://id.norge.no/los"));
        Integer result = getCountFromSelectResponse(response);
        Assertions.assertEquals(651, result);
    }

    @Test
    void countArchivedBetaLOSTriples() throws Exception {
        String response = TestQuery.sendQuery(countQuery("https://id.norge.no/los-beta-archived"));
        Integer result = getCountFromSelectResponse(response);
        Assertions.assertEquals(437, result);
    }

}
