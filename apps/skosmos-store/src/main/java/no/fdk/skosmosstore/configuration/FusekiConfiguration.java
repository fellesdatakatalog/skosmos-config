package no.fdk.skosmosstore.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application.fuseki")
public class FusekiConfiguration {
    private String realm;
    private Short port;
    private String contextPath;
    private String storePath;
    private String datasetName;
    private Boolean enableVerboseLogging;
}
