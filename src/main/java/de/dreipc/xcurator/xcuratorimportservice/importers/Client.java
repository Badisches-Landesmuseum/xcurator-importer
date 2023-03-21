package de.dreipc.xcurator.xcuratorimportservice.importers;

import de.dreipc.xcurator.xcuratorimportservice.models.DataSource;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;

public interface Client {
    DataSource getDataSource();

    LanguageCode getDefaultLanguage();

    int importObjects();

    int getTotalAvailableData();
}
