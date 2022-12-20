package de.dreipc.xcurator.xcuratorimportservice.importers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.APProperties;
import de.dreipc.xcurator.xcuratorimportservice.exceptions.AllardPiersonClientException;
import de.dreipc.xcurator.xcuratorimportservice.exceptions.ExpoDBClientException;
import de.dreipc.xcurator.xcuratorimportservice.models.*;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.services.EpochService;
import de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil;
import de.dreipc.xcurator.xcuratorimportservice.utils.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.StreamSupport;

import static de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil.getStringOrDefault;

@Service
@Slf4j
public class APClient implements Client {

    private final ObjectMapper jsonMapper;
    private final APProperties properties;
    private final AssetService assetService;
    private final StoreMuseumObjectCommand storeMuseumObjectCommand;


    public APClient(APProperties properties, ObjectMapper jsonMapper, AssetService assetService, StoreMuseumObjectCommand storeMuseumObjectCommand) {
        this.jsonMapper = jsonMapper;
        this.properties = properties;
        this.assetService = assetService;
        this.storeMuseumObjectCommand = storeMuseumObjectCommand;
    }

    private static List<MuseumImage> getListMuseumsImagesOrDefault(JsonNode node, String listFieldName, ObjectId museumObjectId, ObjectId projectId) {
        try {
            var imagesNode = node.get(listFieldName);
            if (imagesNode instanceof ArrayNode listImagesNode) {
                return StreamSupport.stream(listImagesNode.spliterator(), false).map(APClient::getMuseumsImage).filter(Objects::nonNull).peek(elem -> elem.sourceId(museumObjectId)).peek(elem -> elem.projectId(projectId)).map(MuseumImage.MuseumImageBuilder::build).toList();
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
            var imageHost = "https://servicetin.adlibhosting.com/te4/wwwopac.ashx?command=getcontent&server=images&width=400&height=1200";
            var fileName = jsonDocument.get("reproduction.identifier_URL").findValue("text").asText();
            var title = jsonDocument.get("reproduction.reference").get("reference_number").findValue("text").asText();
            var url = new URL(imageHost + "&value=" + fileName);
            return MuseumImage.builder().id(id).fileName(fileName).title(title).sourceUrl(url).createdAt(Instant.now()).updatedAt(Instant.now());
        } catch (Exception e) {
            log.error("Image could not be parsed: ", e);
            return null;
        }

    }

    public DateRange getDateStampOrDefault(JsonNode node) {
        try {


            var beginYear = node.get("production.date.start").findValue("text").asInt();
            var endYear = node.get("production.date.end").findValue("text").asInt();

            var begin = JsonParserUtil.getInstantOrDefault(beginYear);
            var end = JsonParserUtil.getInstantOrDefault(endYear);

            var epoch = EpochService.extractEpoch(end);

            return DateRange
                    .builder()
                    .start(begin)
                    .end(end)
                    .epoch(epoch)
                    .build();

        } catch (Exception e) {
            log.warn("Can't fetch " + node + ". Fallback default. Error: " + e.getMessage());
            return JsonParserUtil.DEFAULT_DATE_STAMP_VALUE;

        }
    }

    @Override
    public int importObjects() {
        try {
            return importObjects(properties.getTinItemCount()).size();
        } catch (Exception e) {
            log.warn("Did not import: " + getClientName() + "cause:", e);
            return 0;
        }
    }

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

    private List<MuseumObject> processAndSave(URL url) {
        try {
            var museumResults = StreamUtil.stream(requestApi(url)).map(this::parseItem).filter(Optional::isPresent).map(Optional::get).toList();

            var saved = storeMuseumObjectCommand.save(museumResults);
            return saved;
        } catch (Exception e) {
            log.error("batch could not be saved", e);
            return new ArrayList<>();
        }
    }

    public ArrayNode requestApi(URL requestUrl) {
        try (var inputStream = StreamUtil.getInputStream(requestUrl)) {
            JsonNode json = jsonMapper.readTree(inputStream);
            ArrayNode items = (ArrayNode) json.findValue("record");
            if (items.size() == 0)
                throw new ArrayIndexOutOfBoundsException("Current query returned 0 items");
            return items;
        } catch (Exception e) {
            log.error("Failed importing following request: {}, cause :", requestUrl, e);
            throw new ExpoDBClientException("Failed importing following request: " + requestUrl + ", cause :", e);
        }

    }

    public int getTotalAvailableData() {
        var requestUrl = buildBatchFetchingUrl(0, 1);
        try (var inputStream = StreamUtil.getInputStream(requestUrl)) {
            JsonNode json = jsonMapper.readTree(inputStream);
            var totalNumberOfAvailableData = (Integer) json.get("adlibJSON").get("diagnostic").get("hits").numberValue();
            if (totalNumberOfAvailableData == 0)
                throw new ArrayIndexOutOfBoundsException("Current query returned 0 items");
            return totalNumberOfAvailableData;
        } catch (Exception e) {
            log.error("Failed fetching available data, following request: {}, cause :", requestUrl, e);
            throw new AllardPiersonClientException("Failed fetching available following request: " + requestUrl + ", cause :", e);
        }
    }

    public URL buildBatchFetchingUrl(int limit, int offset) {

        var requestUrl = new StringBuilder(properties.getTinHost().toString()).append("&limit=").append(limit).append("&startfrom=").append(offset);

        try {
            return new URL(requestUrl.toString());
        } catch (MalformedURLException e) {
            log.error("Unable to build Allard Pierson api url. Error: " + e.getMessage());
            throw new AllardPiersonClientException(e);
        }
    }

    Optional<MuseumResult> parseItem(JsonNode documentJson) {

        try {

            var id = new ObjectId();
            var projectId = assetService.getProperties().getProjectId();

            var externalId = getStringOrDefault(documentJson, "@priref");

            var titles = documentJson.get("title").findValues("text").stream().map(text -> TextContent.builder()
                    .content(text.asText())
                    .projectId(projectId)
                    .languageCode(LanguageCode.DUTCH.toString())
                    .sourceId(id)
                    .textType(TextType.TITLE)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .originalText(true)
                    .build()).toList();

            var dateRange = getDateStampOrDefault(documentJson);

            var images = getListMuseumsImagesOrDefault(documentJson, "Reproduction", id, projectId);
            var museumObject = MuseumObject.builder().id(id)
                    .projectId(projectId)
                    .externalId(externalId)
                    .assetIds(images.stream().map(MuseumImage::getId).toList())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .dateRange(dateRange)
                    .location(Location.builder().build())
                    .build();

            return Optional.of(MuseumResult.builder().museumObject(museumObject).images(images).texts(titles).build());


        } catch (Exception e) {
            log.error("Unable to parse item, Error: " + e.getMessage());
            return Optional.empty();
        }

    }

    @Override
    public ClientName getClientName() {
        return ClientName.TIN;
    }
}
