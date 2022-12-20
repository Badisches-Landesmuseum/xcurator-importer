package de.dreipc.xcurator.xcuratorimportservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.models.DateRange;
import de.dreipc.xcurator.xcuratorimportservice.models.Location;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static de.dreipc.xcurator.xcuratorimportservice.services.EpochService.extractEpoch;

@Slf4j
public class JsonParserUtil {

    public static final DateRange DEFAULT_DATE_STAMP_VALUE = DateRange.builder().build();
    private static final String DEFAULT_STRING_VALUE = "";
    private static final Integer DEFAULT_INT_VALUE = null;
    private static final Float DEFAULT_FLOAT_VALUE = null;
    private static final Instant DEFAULT_INSTANT_VALUE = null;
    private static final URL DEFAULT_URL_VALUE = null;

    private static final Location DEFAULT_UNKNOWN_LOCATION = Location.builder().name("Unbekannter Ort").build();


    private JsonParserUtil() {
        // empty
    }

    public static String getStringOrDefault(JsonNode node, String fieldName) {
        var defaultValue = DEFAULT_STRING_VALUE;
        try {
            defaultValue = node
                    .get(fieldName)
                    .asText(defaultValue)
                    .trim();
        } catch (Exception e) {
            log.debug("Can't fetch " + fieldName + ". Fallback default. Error: " + e.getMessage());
        }

        return defaultValue;
    }

    public static Integer getIntOrDefault(JsonNode node, String fieldName) {
        var defaultValue = DEFAULT_INT_VALUE;
        try {
            defaultValue = node
                    .get(fieldName)
                    .asInt();
        } catch (Exception e) {
            log.debug("Can't fetch " + fieldName + ". Fallback default. Error: " + e.getMessage());
        }
        if (defaultValue == 0)
            defaultValue = null;
        return defaultValue;
    }

    public static URL getURLOrDefault(JsonNode node, String fieldName) {
        try {
            var urlString = getStringOrDefault(node, fieldName);
            return new URL(urlString);
        } catch (Exception e) {
            log.error("Can‘t parse url of json node (" + fieldName + "). Error: " + e.getMessage());
            return DEFAULT_URL_VALUE;
        }
    }


    public static Float getFloatOrDefault(JsonNode node, String fieldName) {
        try {
            var stringValue = getStringOrDefault(node, fieldName);
            return Float.parseFloat(stringValue.trim());
        } catch (Exception e) {
            log.debug("Can't fetch " + fieldName + ". Fallback default. Error: " + e.getMessage());
            return DEFAULT_FLOAT_VALUE;
        }
    }


    public static Instant getInstantOrDefault(int year) {
        try {
            return LocalDate
                    .of(year, 1, 1)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            log.debug("Can't parse year " + year + ". Fallback default. Error: " + e.getMessage());
            return DEFAULT_INSTANT_VALUE;
        }
    }

    public static Instant getInstantOrDefault(JsonNode node, String fieldName) {
        try {
            var dateString = node
                    .get(fieldName)
                    .asText("");
            var year = Integer.valueOf(dateString.trim());
            return getInstantOrDefault(year);
        } catch (Exception e) {
            log.debug("Can't fetch " + fieldName + ". Fallback default. Error: " + e.getMessage());
            return DEFAULT_INSTANT_VALUE;
        }
    }

    public static List<String> getListOrDefault(JsonNode jsonNode, String listFieldName, String fieldName) {
        List<String> defaultValue = new ArrayList<>();
        try {
            var listNode = jsonNode.get(listFieldName);
            if (listNode instanceof ArrayNode arrayListNode) {
                defaultValue = StreamSupport
                        .stream(arrayListNode.spliterator(), false)
                        .map(element -> getStringOrDefault(element, fieldName))
                        .filter(elem -> !elem.isEmpty())
                        .toList();
            } else {
                log.debug("Json node ( " + listFieldName + ") is not a list node.");
            }
        } catch (Exception e) {
            log.error(
                    "Error during string list parsing of Json node ( " + listFieldName + "). Error: " + e.getMessage());
        }

        return defaultValue;
    }

    public static DateRange getDateStampOrDefault(JsonNode node, String fieldName) {
        try {
            var dateNode = node.get(fieldName);
            if (dateNode instanceof ArrayNode listDateNode) {
                return StreamSupport.stream(listDateNode.spliterator(), false)
                        .map(dateElemNode -> {
                            var earliestDate = getInstantOrDefault(dateElemNode, "beginn");
                            var latestYear = getInstantOrDefault(dateElemNode, "ende");
                            var term = getStringOrDefault(dateElemNode, "term");

                            if (earliestDate == null || latestYear == null || term.isEmpty()) {
                                log.debug(
                                        "Unable to parse dateStamp because of invalid data. start-date: " + earliestDate + " end-date: " + latestYear + " term: " + term);
                                return DEFAULT_DATE_STAMP_VALUE;
                            }
                            var epoch = extractEpoch(latestYear);

                            return DateRange
                                    .builder()
                                    .start(earliestDate)
                                    .end(latestYear)
                                    .name(term)
                                    .epoch(epoch)
                                    .build();
                        })
                        .findFirst()
                        .orElse(DEFAULT_DATE_STAMP_VALUE);
            } else {
                log.debug("Date node is not of type ArrayNode. skip parsing");
                return DEFAULT_DATE_STAMP_VALUE;
            }

        } catch (Exception e) {
            log.debug("Can't fetch " + fieldName + ". Fallback default. Error: " + e.getMessage());
            return DEFAULT_DATE_STAMP_VALUE;
        }
    }

    public static Location getLocationOrDefault(JsonNode jsonNode, String fieldName) {
        try {
            var locationNode = jsonNode.get(fieldName);
            if (locationNode instanceof ArrayNode locationArrayNode) {
                return StreamSupport.stream(locationArrayNode.spliterator(), false)
                        .map(node -> {
                            var name = getStringOrDefault(node, "term");
                            var longitude = getFloatOrDefault(node, "lon");
                            var latitude = getFloatOrDefault(node, "lat");
                            var locationType = getStringOrDefault(node, "typ"); // Currently not used
                            return Location.builder()
                                    .name(name)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .build();
                        }).findFirst().orElse(DEFAULT_UNKNOWN_LOCATION);
            } else {
                log.debug(
                        "location json node (" + fieldName + ") is not of type ArrayNode. Can't extract location information.");
                return DEFAULT_UNKNOWN_LOCATION;
            }
        } catch (Exception e) {
            log.error("Error during location parsing of Json node ( " + fieldName + "). Error: " + e.getMessage());
            return DEFAULT_UNKNOWN_LOCATION;
        }

    }
}
