package de.dreipc.xcurator.xcuratorimportservice.importers;


import com.fasterxml.jackson.core.JsonProcessingException;
import de.dreipc.xcurator.xcuratorimportservice.commands.StoreMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.models.*;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import de.dreipc.xcurator.xcuratorimportservice.services.EpochService;
import de.dreipc.xcurator.xcuratorimportservice.utils.FileUtil;
import de.dreipc.xcurator.xcuratorimportservice.utils.JsonParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class APXMLClient {

    private static final String XML_FILE_PATH = "files/wwwopac_TIN_limit100000.xml";
    private final AssetService assetService;
    private final MuseumObjectRepository museumObjectRepository;

    private final StoreMuseumObjectCommand storeMuseumObjectCommand;

    public APXMLClient(AssetService assetService, MuseumObjectRepository museumObjectRepository, StoreMuseumObjectCommand storeMuseumObjectCommand) {
        this.assetService = assetService;
        this.museumObjectRepository = museumObjectRepository;
        this.storeMuseumObjectCommand = storeMuseumObjectCommand;
    }

    public int importObjects(int totalCount) {
        var xmlContent = FileUtil.getResourceFileAsInputStream(XML_FILE_PATH);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            var document = builder.parse(xmlContent);
            var nodeList = document
                    .getDocumentElement()
                    .getChildNodes()
                    .item(0)
                    .getChildNodes();

            var museumObjects = new ArrayList<Optional<MuseumResult>>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    museumObjects.add(parseItem(node));
                }
            }

            return processAndSave(museumObjects).size();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }



    private List<MuseumObject> processAndSave(List<Optional<MuseumResult>> objects) {
        try {
            var museumResults = objects.stream().filter(Optional::isPresent).map(Optional::get).toList();

            var notExistingMuseumResults = museumResults.stream().filter(obj -> !museumObjectRepository.existsByExternalId(obj.getMuseumObject().getExternalId())).toList();
            var diff = museumResults.size() - notExistingMuseumResults.size();
            log.info("Skipping " + diff + " Objects, due they already exist. Import " + notExistingMuseumResults.size() +" additional Objects ");
            if(notExistingMuseumResults.size() == 0){
                log.info("Nothing to import. Skip");
                return new ArrayList<>();
            }
            var saved = storeMuseumObjectCommand.save(notExistingMuseumResults);
            return saved;
        } catch (Exception e) {
            log.error("batch could not be saved", e);
            return new ArrayList<>();
        }
    }

    Optional<MuseumResult> parseItem(Node node) {

        try {
            Element elem = (Element) node;

            var id = new ObjectId();

            var projectId = this.assetService
                    .getProperties()
                    .getProjectId();

            // Get the value of the ID attribute.
            String externalId = node
                    .getAttributes()
                    .getNamedItem("priref")
                    .getNodeValue();


            String titleString = elem
                    .getElementsByTagName("title")
                    .item(0)
                    .getChildNodes()
                    .item(0)
                    .getNodeValue();


            var title = TextContent
                            .builder()
                            .content(titleString)
                            .projectId(projectId)
                            .languageCode(LanguageCode.DUTCH.toString())
                            .sourceId(id)
                            .textType(TextType.TITLE)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .originalText(true)
                            .build();

            var dateRange = getDateStampOrDefault(elem);

            var images = getListMuseumsImagesOrDefault(elem, id, projectId);
            var museumObject = MuseumObject
                    .builder()
                    .id(id)
                    .projectId(projectId)
                    .externalId(externalId)
                    .assetIds(images
                                      .stream()
                                      .map(MuseumImage::getId)
                                      .toList())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .dateRange(dateRange)
                    .location(Location
                                      .builder()
                                      .build())
                    .build();

            return Optional.of(MuseumResult
                                       .builder()
                                       .museumObject(museumObject)
                                       .images(images)
                                       .texts(List.of(title))
                                       .build());
        } catch (Exception e) {
            log.error("Unable to parse item, Error: " + e.getMessage());
            return Optional.empty();
        }
    }


    private static List<MuseumImage> getListMuseumsImagesOrDefault(Element element, ObjectId museumObjectId, ObjectId projectId) {
        try {
            var imagesNode = element.getElementsByTagName("Reproduction");
            var images = new ArrayList<MuseumImage>();
            for(int i = 0; i < imagesNode.getLength(); i++){
                Node node = imagesNode.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    var museumImageBuilder = getMuseumsImage(((Element) node));
                    museumImageBuilder.sourceId(museumObjectId);
                    museumImageBuilder.projectId(projectId);

                    images.add(museumImageBuilder.build());
                }
            }
            return images;
        } catch (Exception e) {
            log.error("Error importing image, cause:", e);
            return new ArrayList<>();
        }
    }


    private static MuseumImage.MuseumImageBuilder getMuseumsImage(Element element) {
        try {
            var id = new ObjectId();
            var imageHost = "https://servicetin.adlibhosting.com/te4/wwwopac.ashx?command=getcontent&server=images&width=400&height=1200";

            var fileName = element
                    .getElementsByTagName("reproduction.identifier_URL")
                    .item(0)
                    .getChildNodes()
                    .item(0)
                    .getNodeValue();

            var title = element
                    .getElementsByTagName("reproduction.reference")
                    .item(0)
                    .getChildNodes()
                    .item(0)
                    .getNodeValue();

            var url = new URL(imageHost + "&value=" + fileName);
            return MuseumImage.builder().id(id).fileName(fileName).title(title).sourceUrl(url).createdAt(Instant.now()).updatedAt(Instant.now());
        } catch (Exception e) {
            log.error("Image could not be parsed: ", e);
            return null;
        }

    }
    private Instant getYear(Element element, String field){
        try{
            var stringValue =  element
                    .getElementsByTagName(field)
                    .item(0)
                    .getChildNodes()
                    .item(0)
                    .getNodeValue();
            var year = Integer.parseInt(stringValue.trim());
            return JsonParserUtil.getInstantOrDefault(year);
        }catch (Exception e)
        {
            return null;
        }
    }

    public DateRange getDateStampOrDefault(Element elem) {
            var begin = getYear(elem, "production.date.start");
            var end = getYear(elem, "production.date.end");

            var epoch = "";
            if(end != null){
                epoch = EpochService.extractEpoch(end);
            } else if (begin != null){
                epoch = EpochService.extractEpoch(end);
            }

            return DateRange
                    .builder()
                    .start(begin)
                    .end(end)
                    .epoch(epoch)
                    .build();
    }
}
