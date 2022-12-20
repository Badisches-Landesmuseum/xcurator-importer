package de.dreipc.xcurator.xcuratorimportservice.config;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "asset-service")
public class AssetServiceProperties {

    String projectId;


    public ObjectId getProjectId() {
        return new ObjectId(projectId);
    }
}
