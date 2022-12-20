package de.dreipc.xcurator.xcuratorimportservice.importers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.ExpoDBProperties;
import de.dreipc.xcurator.xcuratorimportservice.exceptions.ExpoDBClientException;
import de.dreipc.xcurator.xcuratorimportservice.models.*;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.services.CountryService;
import de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil;
import de.dreipc.xcurator.xcuratorimportservice.utils.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.StreamSupport;

import static de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil.*;

@Slf4j
@Service
public class ExpoDBClient implements Client {
    private final ExpoDBProperties properties;
    private final StoreMuseumObjectCommand storeMuseumObjectCommand;

    private final ObjectMapper jsonMapper;
    private final AssetService assetService;

    private final MuseumObjectRepository museumObjectRepository;

    private final CountryService countryService;


    public ExpoDBClient(ExpoDBProperties properties, StoreMuseumObjectCommand storeMuseumObjectCommand, ObjectMapper jsonMapper, AssetService assetService, MuseumObjectRepository museumObjectRepository, CountryService countryService) {
        this.properties = properties;
        this.storeMuseumObjectCommand = storeMuseumObjectCommand;
        this.jsonMapper = jsonMapper;
        this.assetService = assetService;
        this.museumObjectRepository = museumObjectRepository;
        this.countryService = countryService;
    }

    private static List<MuseumImage> getListMuseumsImagesOrDefault(JsonNode node, String listFieldName, ObjectId museumObjectId, ObjectId projectId) {
        try {
            var imagesNode = node.get(listFieldName);
            if (imagesNode instanceof ArrayNode listImagesNode) {
                return StreamSupport.stream(listImagesNode.spliterator(), false)
                        .map(ExpoDBClient::getMuseumsImage)
                        .filter(Objects::nonNull).peek(elem -> elem.sourceId(museumObjectId))
                        .peek(elem -> elem.projectId(projectId))
                        .map(MuseumImage.MuseumImageBuilder::build)
                        .toList();
            } else {
                log.error("Json Node (" + listFieldName + ") in not of type ArrayNode. Skip parsing.");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Error importing image, cause:", e);
            throw e;
        }
    }

    private static MuseumImage.MuseumImageBuilder getMuseumsImage(JsonNode jsonDocument) {
        try {
            var id = new ObjectId();
            var type = JsonParserUtil.getStringOrDefault(jsonDocument, "typ");
            if (!"Bild".equalsIgnoreCase(type)) {
                log.info("Currently unsupported type (" + type + "). skip parsing.");
                return null;
            }

            var urlString = JsonParserUtil.getStringOrDefault(jsonDocument, "link");
            urlString = urlString.replace("width=400", "width=1200");
            var url = new URL(urlString);

            var fileName = JsonParserUtil.getStringOrDefault(jsonDocument, "name");
            var title = JsonParserUtil.getStringOrDefault(jsonDocument, "term");


            return MuseumImage.builder()
                    .id(id)
                    .fileName(fileName)
                    .title(title)
                    .sourceUrl(url)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now());

        } catch (Exception e) {
            log.error("Error importing image, cause:", e);
            return null;
        }
    }

    private ArrayNode requestApi(URL requestUrl) {
        try (var inputStream = StreamUtil.getInputStream(requestUrl)) {
            JsonNode json = jsonMapper.readTree(inputStream);
            ArrayNode items = (ArrayNode) json.findValue("records");
            if (items.size() == 0)
                throw new ArrayIndexOutOfBoundsException("Current query returned 0 items");
            return items;
        } catch (Exception e) {
            log.error("Failed importing following request: {}, cause :", requestUrl, e);
            throw new ExpoDBClientException("Failed importing following request: " + requestUrl + ", cause :", e);
        }
    }

    public ExpoDBProperties getProperties() {
        return this.properties;
    }

    private List<MuseumObject> processAndSave(URL url) {
        try {
            var museumResults = StreamUtil.stream(requestApi(url)).map(this::parseToMuseumObject).filter(Optional::isPresent).map(Optional::get).toList();

            var notExistingMuseumResults = museumResults.stream().filter(obj -> !museumObjectRepository.existsByExternalId(obj.getMuseumObject().getExternalId())).toList();
            var diff = museumResults.size() - notExistingMuseumResults.size();
            log.info("Skipping " + diff + " Objects, due they already exist. Import " + notExistingMuseumResults.size() +" additional Objects ");
            if(notExistingMuseumResults.size() == 0){
                log.info("Nothing to import. Skip");
                return new ArrayList<>();
            }
            countryService.names(notExistingMuseumResults);
            var saved = storeMuseumObjectCommand.save(notExistingMuseumResults);
            return saved;
        } catch (Exception e) {
            log.error("batch could not be saved", e);
            return new ArrayList<>();
        }
    }


    private URL buildBatchFetchingUrl(int limit, int offset) {

        try {
            return new URL(properties.getHost().toString() + "&fst=" + offset + "&len=" + limit);

        } catch (MalformedURLException e) {
            log.error("Unable to build blm api url. Error: " + e.getMessage());
            throw new ExpoDBClientException(e);
        }
    }

    @Override
    public int importObjects() {
        try {
            return importObjects(properties.getMaxItemCount()).size();
        } catch (Exception e) {
            log.warn("Did not import: " + getClientName() + "cause:", e);
            return 0;
        }
    }

    @Transactional
    public List<MuseumObject> importObjects(int totalCount) {
        if (totalCount == 0) {
            log.info("No data were selected for: " + getClientName() + " import");
            return Collections.emptyList();
        }

        var totalNumberOfAvailableData = getTotalAvailableData();
        log.info("There are  " + totalNumberOfAvailableData + " available data in their storage.");
        var total = totalNumberOfAvailableData < totalCount ? totalNumberOfAvailableData : totalCount;
        log.info("Start importing from: " + getClientName());

        return StreamUtil.getUrlStreams(total, this::buildBatchFetchingUrl)
                .map(this::processAndSave)
                .flatMap(Collection::stream)
                .toList();
    }

    public int getTotalAvailableData() {
        var requestUrl = buildBatchFetchingUrl(1, 0);
        try (var inputStream = StreamUtil.getInputStream(requestUrl)) {
            JsonNode json = jsonMapper.readTree(inputStream);
            var totalNumberOfAvailableData = Integer.parseInt(json.get("head").get("numFound").asText().replace("", ""));
            if (totalNumberOfAvailableData == 0)
                throw new ArrayIndexOutOfBoundsException("Current query returned 0 items");
            return totalNumberOfAvailableData;
        } catch (Exception e) {
            log.error("Failed fetching available data, following request: {}, cause :", requestUrl, e);
            throw new ExpoDBClientException("Failed fetching available following request: " + requestUrl + ", cause :", e);
        }
    }

    private Optional<MuseumResult> parseToMuseumObject(JsonNode documentJson) {

        try {

            var id = new ObjectId();
            var externalId = getStringOrDefault(documentJson, "imdasid");

            var projectId = this.assetService.getProperties().getProjectId();
            if (externalId.isEmpty())
                throw new IllegalArgumentException("externalId is required but null. Unable to import this invalid data entry.");

            var title = TextContent.builder()
                    .id(new ObjectId())
                    .projectId(projectId)
                    .sourceId(id)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .content(getStringOrDefault(documentJson, "titel"))
                    .languageCode(LanguageCode.GERMAN.label)
                    .originalText(true)
                    .textType(TextType.TITLE)
                    .build();

            var description = TextContent.builder()
                    .id(new ObjectId())
                    .sourceId(id)
                    .projectId(projectId)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .content(getStringOrDefault(documentJson, "textdeutsch"))
                    .languageCode(LanguageCode.GERMAN.label)
                    .textType(TextType.DESCRIPTION)
                    .originalText(true)
                    .build();

            var dateRange = getDateStampOrDefault(documentJson, "datierung");

            var location = getLocationOrDefault(documentJson, "ort");

            var keywords = getListOrDefault(documentJson, "schlagworte", "term");
            var materials = getListOrDefault(documentJson, "material", "term");
            var technik = getListOrDefault(documentJson, "technik", "term");

            var images = getListMuseumsImagesOrDefault(documentJson, "medium", id, projectId);

            if (images.isEmpty())
                throw new IllegalArgumentException("Images in museum object must not be empty. Unable to import this invalid data entry.");

            var museumObject = MuseumObject.builder()
                    .id(id)
                    .externalId(externalId)
                    .projectId(projectId)
                    .dateRange(dateRange)
                    .assetIds(images.stream().map(MuseumImage::getId).toList())
                    .location(location)
                    .keywords(keywords).createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .techniques(technik)
                    .materials(materials)
                    .build();

            return Optional.of(MuseumResult.builder().museumObject(museumObject).texts(List.of(title, description)).images(images).build());

        } catch (Exception e) {
            log.error("Unable to parse museum item, Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public ClientName getClientName() {
        return ClientName.EXPODB;
    }


}
