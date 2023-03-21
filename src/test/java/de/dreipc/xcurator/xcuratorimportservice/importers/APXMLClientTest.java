package de.dreipc.xcurator.xcuratorimportservice.importers;

import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class APXMLClientTest {

    private APXMLClient client;

    @BeforeEach
    public void setUp() {
        var assetService = mock(AssetService.class);
        var museumObjectRepository = mock(MuseumObjectRepository.class);
        var storeMuseumObjectCommand = mock(StoreMuseumObjectCommand.class);
        client = new APXMLClient(assetService, museumObjectRepository, storeMuseumObjectCommand);
    }

    @Test
    void importObjects() {
        client.importObjects(-1);
    }

}
