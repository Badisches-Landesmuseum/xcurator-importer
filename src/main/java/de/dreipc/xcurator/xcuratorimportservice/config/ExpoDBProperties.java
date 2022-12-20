package de.dreipc.xcurator.xcuratorimportservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;


@Data
@Configuration
@ConfigurationProperties(prefix = "expodb")
public class ExpoDBProperties {
    URL host;
    int maxItemCount = 10000;
}
