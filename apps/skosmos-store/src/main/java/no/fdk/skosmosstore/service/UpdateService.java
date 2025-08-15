package no.fdk.skosmosstore.service;

import lombok.RequiredArgsConstructor;
import no.fdk.skosmosstore.configuration.FusekiConfiguration;
import no.fdk.skosmosstore.fuseki.action.CompactAction;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);
    private final FusekiConfiguration fusekiConfiguration;
    private final CompactAction compactAction;

    private RDFConnection fusekiConnection() {
        return RDFConnectionFuseki.create()
                .destination("http://localhost:8080/fuseki/skosmos")
                .build();
    }

    private void updateGraph(String graphName, Model catalogModel) {
        try (RDFConnection conn = fusekiConnection() ) {
            conn.begin(ReadWrite.WRITE);
            conn.put(graphName, catalogModel);
        } catch (Exception exception) {
            log.error("update of graph {} failed", graphName, exception);
        }
    }

    public Model readTurtleResource(String resourcePath) {
        Model m = ModelFactory.createDefaultModel();
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            InputStream inputStream = resource.getInputStream();
            m.read(inputStream, "", "TURTLE");
        } catch (Exception ex) {
            log.error("unable to read turtle for {}", resourcePath);
        }
        return m;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void updateGraphs() {
        log.info("updating graphs");
        updateGraph("https://id.norge.no/los", readTurtleResource("LOS/los-export.ttl"));

        String path = "%s/%s".formatted(fusekiConfiguration.getStorePath(), fusekiConfiguration.getDatasetName());
        compactAction.compact(path);
    }

}
