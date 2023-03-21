package de.dreipc.xcurator.xcuratorimportservice.importers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.APProperties;
import de.dreipc.xcurator.xcuratorimportservice.config.AssetServiceProperties;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class APSparqlClientTest {

    private final APSparqlClient client;
    private final URI endpoint = new URI(
            "https://lod.uba.uva.nl/_api/datasets/UB-UVA/Beeldbank/services/virtuoso/sparql");


    APSparqlClientTest() throws URISyntaxException {
        var assetService = Mockito.mock(AssetService.class);
        var properties = Mockito.mock(AssetServiceProperties.class);
        var apProperties = Mockito.mock(APProperties.class);

        var museumObjectRepository = Mockito.mock(MuseumObjectRepository.class);
        when(museumObjectRepository.existsByExternalId(anyString())).thenReturn(false);


        var storeCommand = Mockito.mock(StoreMuseumObjectCommand.class);
        when(storeCommand.save(anyList())).thenReturn(new ArrayList<>());

        when(assetService.getProperties()).thenReturn(properties);
        when(properties.getProjectId()).thenReturn(new ObjectId());

        when(apProperties.getSparqlEndpoint()).thenReturn(endpoint);
        when(apProperties.getBeeldbankItemCount()).thenReturn(100);

        this.client = new APSparqlClient(new ObjectMapper(), assetService, apProperties, storeCommand,
                museumObjectRepository);
    }
    
    @Test
    void totalCount() {
        var count = client.getTotalAvailableData();
        Assert.isTrue(count > 1000, "Count should be more than 1000.");
    }

    @Test
    void getData() {
        client.getData(10, 0);
    }

    @Test
    void importData() {
        client.importObjects(10);
    }
}
