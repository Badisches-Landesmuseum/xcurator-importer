package de.dreipc.xcurator.xcuratorimportservice.importers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.APProperties;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntitiesRequester;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = {APProperties.class, NamedEntitiesRequester.class, StoreMuseumObjectCommand.class, AssetService.class, AssetServiceProperties.class})
@EnableConfigurationProperties
@Import(UnitTestConfiguration.class)
@Disabled("due to tin endpoint error")
class TinClientTest {

    private static APClient client;

    @BeforeAll
    private static void setUp(@Autowired APProperties _properties,
                              @Autowired AssetService _assetService,
                              @Autowired StoreMuseumObjectCommand _storeMuseumObjectCommand) {
        client = new APClient(_properties, new ObjectMapper(), _assetService, _storeMuseumObjectCommand);
    }

    @Test
    @Disabled("API Not available anymore")
    void importObjects() {
        assertTrue(client.importObjects() > 0);
    }

}
