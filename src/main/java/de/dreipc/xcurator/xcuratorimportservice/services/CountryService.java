package de.dreipc.xcurator.xcuratorimportservice.services;

import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObject;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumResult;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiData;
import io.github.coordinates2country.Coordinates2Country;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final WikiData wikidata;

    public CountryService(WikiData wikidata) {
        this.wikidata = wikidata;
    }

    public List<String> names(List<MuseumResult> museumResults) {
        var locations = museumResults.stream()
                    .map(MuseumResult::getMuseumObject)
                     .map(MuseumObject::getLocation)
                     .filter(loc -> loc.getLatitude() != null)
                     .filter(loc -> loc.getLongitude() != null)
                .toList();

        var wikiIds = locations
                .stream()
                .map(location -> Coordinates2Country.countryQID(location.getLatitude(), location.getLongitude()))
                .map(id -> "Q" + id)
                .toList();
        var locationNames = wikidata.names(wikiIds);
        for (int i = 0;  i < locations.size(); i++)
            locations.get(i).setCountryName(locationNames.get(i));

        return locationNames;
    }

}
