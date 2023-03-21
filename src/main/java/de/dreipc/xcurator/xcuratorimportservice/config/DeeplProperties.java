package de.dreipc.xcurator.xcuratorimportservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "deepl")
public class DeeplProperties {
    public String authKey;
}
