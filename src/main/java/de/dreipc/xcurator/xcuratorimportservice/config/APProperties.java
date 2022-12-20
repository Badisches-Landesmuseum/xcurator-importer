package de.dreipc.xcurator.xcuratorimportservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URL;

@Data
@Configuration
@ConfigurationProperties(prefix = "ap")
public class APProperties {
    URL tinHost;
    URI sparqlEndpoint;
    int tinItemCount;
    int beeldbankItemCount;
}
