package de.dreipc.xcurator.xcuratorimportservice.importers;

import de.dreipc.xcurator.xcuratorimportservice.models.ClientName;

public interface Client {
    ClientName getClientName();

    int importObjects();

    int getTotalAvailableData();
}
