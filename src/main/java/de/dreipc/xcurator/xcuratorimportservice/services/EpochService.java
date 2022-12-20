package de.dreipc.xcurator.xcuratorimportservice.services;

import de.dreipc.xcurator.xcuratorimportservice.models.Epoch;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class EpochService {

    private static final List<Epoch> epochs = List.of(
            new Epoch("Ur - und Frühgeschichte", Integer.MIN_VALUE, -3000),
            new Epoch("Antike", -3000, 565),
            new Epoch("Frühes Mittelalter", 565, 950),
            new Epoch("Romanik", 950, 1200),
            new Epoch("Gotik", 1200, 1400),
            new Epoch("Renaissance", 1400, 1575),
            new Epoch("Barock", 1575, 1770),
            new Epoch("Romantik", 1770, 1855),
            new Epoch("Moderne", 1855, 1945),
            new Epoch("Postmoderne", 1945, Integer.MAX_VALUE)
    );

    public static Epoch byYear(int givenYear) {
        return epochs
                .stream()
                .filter(epoch -> epoch.start() <= givenYear && epoch.end() > givenYear)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Given input year leads to unexpected error. Please check"));
    }

    public static Epoch byInstant(Instant instant) {
        var givenYear = instant.atZone(ZoneOffset.UTC).getYear();
        return epochs
                .stream()
                .filter(epoch -> epoch.start() <= givenYear && epoch.end() > givenYear)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Given input year leads to unexpected error. Please check"));
    }

    public static String extractEpoch(Instant dateRange) {
        try {
            return byInstant(dateRange).name();
        } catch (Exception e) {
            return "";
        }
    }

    public Optional<Epoch> byName(String name) {
        return epochs
                .stream()
                .filter(epoch -> epoch
                        .name()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

}
