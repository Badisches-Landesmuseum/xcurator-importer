package de.dreipc.xcurator.xcuratorimportservice.importers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.APProperties;
import de.dreipc.xcurator.xcuratorimportservice.models.*;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static de.dreipc.xcurator.xcuratorimportservice.services.EpochService.extractEpoch;


@Slf4j
@Service
public class APSparqlClient implements Client {

    private final ObjectMapper jsonMapper;
    private final AssetService assetService;

    private final APProperties properties;
    private final StoreMuseumObjectCommand storeMuseumObjectCommand;

    private final MuseumObjectRepository museumObjectRepository;


    public APSparqlClient(ObjectMapper jsonMapper, AssetService assetService, APProperties properties, StoreMuseumObjectCommand storeMuseumObjectCommand, MuseumObjectRepository museumObjectRepository) {
        this.jsonMapper = jsonMapper;
        this.assetService = assetService;
        this.properties = properties;
        this.storeMuseumObjectCommand = storeMuseumObjectCommand;
        this.museumObjectRepository = museumObjectRepository;
    }

    public int getTotalAvailableData() {
        var client = HttpClient.newHttpClient();
        var query = APSparqlQuery.totalCountQuery();
        try {
            var request = buildRequest(query);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new IllegalArgumentException("Request was not successful. Body: " + response.body());
            var json = jsonMapper.readTree(response.body());
            if (json instanceof ArrayNode jsonList)
                return jsonList
                        .get(0)
                        .get("totalCount")
                        .asInt();
            else
                log.warn("Json Schema has been changed. Total count can't be read.");

        } catch (Exception e) {
            log.error("Unable to fetch data. Error: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int importObjects() {
        try {
            return importObjects(properties.getBeeldbankItemCount());

        } catch (Exception e) {
            log.warn("Did not import: " + getDataSource() + "cause:", e);
            return 0;
        }
    }

    @Transactional
    public int importObjects(int totalCount) {
        if (totalCount == 0) {
            log.info("No data were selected for: " + getDataSource() + " import");
            return 0;
        }

        var totalNumberOfAvailableData = getTotalAvailableData();
        if (totalCount < 0)
            totalCount = totalNumberOfAvailableData;
        log.info("There are  " + totalNumberOfAvailableData + " available data in their storage.");
        var total = Math.min(totalNumberOfAvailableData, totalCount);
        log.info("Start importing from: " + getDataSource());

        var batchSize = 480;
        var batchCount = totalCount / batchSize;

        var importedCount = 0;
        for (int i = 0; i <= batchCount; i++) {
            var skip = i * batchSize;
            var take = Math.min(batchSize, total - skip);

            var museumResults = getData(take, skip);

            var notExistingMuseumResults = museumResults.stream().filter(obj -> !museumObjectRepository.existsByExternalId(obj.getMuseumObject().getExternalId())).toList();
            var diff = museumResults.size() - notExistingMuseumResults.size();
            log.info("Skipping " + diff + " Objects, due they already exist. Import " + notExistingMuseumResults.size() + " additional Objects ");
            if (notExistingMuseumResults.size() == 0) {
                log.info("Nothing to import. Skip");
            } else {
                storeMuseumObjectCommand.save(notExistingMuseumResults);
                importedCount += take;
            }
        }
        return importedCount;
    }

    public List<MuseumResult> getData(int take, int skip) {
        var client = HttpClient.newHttpClient();
        var query = APSparqlQuery.getDataBatchedQuery(take, skip);
        try {
            var request = buildRequest(query);
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new IllegalArgumentException("Request was not successful. Body: " + response.body());
            var json = jsonMapper.readTree(response.body());
            if (json instanceof ArrayNode jsonList) {
                return StreamSupport
                        .stream(jsonList.spliterator(), false)
                        .map(this::museumResult)
                        .toList();
            } else
                log.warn("Json Schema has been changed. Total count can't be read.");

        } catch (Exception e) {
            log.error("Unable to fetch data. Error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private DateRange dateRange(Integer begin, Integer end) {
        var builder = DateRange.builder();
        var term = "";
        if (begin != null) {
            var beginDate = LocalDate
                    .of(begin, 1, 1)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);
            builder.start(beginDate);
            term += begin;
        }
        if (end != null) {
            var endDate = LocalDate
                    .of(end, 1, 1)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);
            builder.end(endDate);
            builder.epoch(extractEpoch(endDate));
            if (term.isEmpty())
                term += end;
            else
                term += " - " + end;
        }
        builder.name(term);

        return builder.build();
    }

    private MuseumResult museumResult(JsonNode jsonNode) {
        var id = new ObjectId();
        var externalId = JsonParserUtil.getStringOrDefault(jsonNode, "identifier");
        var projectId = this.assetService
                .getProperties()
                .getProjectId();
        if (externalId.isEmpty())
            throw new IllegalArgumentException(
                    "externalId is required but null. Unable to import this invalid data entry.");

        var begin = JsonParserUtil.getIntOrDefault(jsonNode, "begin");
        var end = JsonParserUtil.getIntOrDefault(jsonNode, "end");
        var dateRange = dateRange(begin, end);


        var located = JsonParserUtil.getStringOrDefault(jsonNode, "located");
        var location = Location
                .builder()
                .name(located)
                .build();

        var object = MuseumObject
                .builder()
                .id(id)
                .externalId(externalId)
                .projectId(projectId)
                .dateRange(dateRange)
                .location(location)
                .keywords(Collections.emptyList())  // not provided
                .techniques(Collections.emptyList()) // not provided
                .materials(Collections.emptyList()) // not provided
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .dataSource(DataSource.BEELDBANK)
                .build();

        var creator = JsonParserUtil.getStringOrDefault(jsonNode, "creator");
        var titleString = JsonParserUtil.getStringOrDefault(jsonNode, "title");
        var descriptionString = JsonParserUtil
                .getStringOrDefault(jsonNode, "descriptions")
                .replace(" |", ".");
        var abstractText = JsonParserUtil.getStringOrDefault(jsonNode, "abstract");

        var title = TextContent
                .builder()
                .id(new ObjectId())
                .projectId(projectId)
                .sourceId(id)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .content(titleString)
                .languageCode(getDefaultLanguage())
                .originalText(true)
                .textType(TextType.TITLE)
                .build();

        var description = TextContent
                .builder()
                .id(new ObjectId())
                .sourceId(id)
                .projectId(projectId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .content(descriptionString)
                .languageCode(getDefaultLanguage())
                .textType(TextType.DESCRIPTION)
                .originalText(true)
                .build();

        var imageUrlString = JsonParserUtil.getStringOrDefault(jsonNode, "iiif");
        var images = new ArrayList<MuseumImage>();
        try {
            var sizeIndex = imageUrlString.lastIndexOf("full");
            var maxSizeUrl = imageUrlString.substring(0, sizeIndex) + "!1200,1200/0/default.jpg";
            var imageUrl = new URL(maxSizeUrl);
            var fileName = imageUrlString.substring(imageUrlString.indexOf("2/") + 2);
            fileName = fileName.substring(0, fileName.indexOf("/"));


            var image = MuseumImage
                    .builder()
                    .id(new ObjectId())
                    .sourceId(id)
                    .sourceUrl(imageUrl)
                    .projectId(projectId)
                    .fileName(fileName)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            images.add(image);
        } catch (MalformedURLException e) {
            log.warn("Image of object (" + externalId + ") has an invalid image url (" + imageUrlString + ")");
        }

        object.setAssetIds(images.stream().map(MuseumImage::getId).toList());
        return MuseumResult
                .builder()
                .museumObject(object)
                .images(images)
                .texts(List.of(title, description))
                .build();
    }

    private HttpRequest buildRequest(String sparqlQuery) {
        Map<String, String> formData = new HashMap<>();
        formData.put("query", sparqlQuery);

        String form = formData
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        return HttpRequest.
                newBuilder(properties.getSparqlEndpoint())
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
    }


    @Override
    public DataSource getDataSource() {
        return DataSource.BEELDBANK;
    }

    @Override
    public LanguageCode getDefaultLanguage() {
        return LanguageCode.nl;
    }
}
