package de.dreipc.xcurator.xcuratorimportservice.importers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.config.ExpoDBProperties;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntitiesRequester;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.services.CountryService;
import de.dreipc.xcurator.xcuratorimportservice.services.EpochService;
import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = {AssetService.class, EpochService.class, NamedEntitiesRequester.class, StoreMuseumObjectCommand.class, AssetServiceProperties.class, ExpoDBProperties.class})
@EnableConfigurationProperties
@Import(UnitTestConfiguration.class)
class ExpoDBClientTest {

    private static final CountryService countryService = new CountryService(new WikiData());
    private static ExpoDBClient client;
    private static ExpoDBProperties properties;
    private static AssetService assetService;
    private static StoreMuseumObjectCommand storeMuseumObjectCommand;

    @BeforeAll
    private static void setUp(@Autowired StoreMuseumObjectCommand _storeMuseumObjectCommand,
                              @Autowired ExpoDBProperties _properties,
                              @Autowired AssetService _assetService) {
        properties = _properties;
        assetService = _assetService;
        storeMuseumObjectCommand = _storeMuseumObjectCommand;

        var museumObjectRepository = mock(MuseumObjectRepository.class);
        when(museumObjectRepository.existsByExternalId(anyString())).thenReturn(false);

        client = new ExpoDBClient(properties, storeMuseumObjectCommand, new ObjectMapper(), assetService,
                                  museumObjectRepository, countryService);
    }

    @Test
    void importObjects() {
        assertTrue(client.importObjects() > 0);
    }

}
