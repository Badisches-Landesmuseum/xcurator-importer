package de.dreipc.xcurator.xcuratorimportservice;

import de.dreipc.xcurator.xcuratorimportservice.importers.APSparqlClient;
import de.dreipc.xcurator.xcuratorimportservice.importers.ExpoDBClient;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Importer implements CommandLineRunner {
    private final ExpoDBClient expoDBClient;
    private final APSparqlClient apSparqlClient;

    private final MuseumObjectRepository museumObjectRepository;

    public Importer(ExpoDBClient expoDBClient, APSparqlClient apSparqlClient, MuseumObjectRepository museumObjectRepository) {
        this.expoDBClient = expoDBClient;
        this.apSparqlClient = apSparqlClient;
        this.museumObjectRepository = museumObjectRepository;
    }

    @Override
    public void run(String... args) {
        if (museumObjectRepository.count() == 0) {
            log.info("no data in mongodb, starting an import");
            importData();
        }
    }

    private void importData() {
        int imported = 0;
        try {
            imported += expoDBClient.importObjects();
            // imported += APClient.importObjects(1000);
            imported += apSparqlClient.importObjects();

            log.info("Done importing: " + imported + " museum objects");
            log.info("Import sequence finished. Imported " + imported + " artifacts.");
        } catch (Exception e) {
            log.warn("Unable to get xcurator data. Reason: " + e.getMessage());
            log.warn("Try again later!");
        }
    }
}
