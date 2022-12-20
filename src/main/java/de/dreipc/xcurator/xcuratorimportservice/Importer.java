package de.dreipc.xcurator.xcuratorimportservice;

import de.dreipc.xcurator.xcuratorimportservice.commands.DeleteMuseumObjectCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.ImporterProperties;
import de.dreipc.xcurator.xcuratorimportservice.importers.APSparqlClient;
import de.dreipc.xcurator.xcuratorimportservice.importers.APXMLClient;
import de.dreipc.xcurator.xcuratorimportservice.importers.ExpoDBClient;
import de.dreipc.xcurator.xcuratorimportservice.services.AssetService;
import dreipc.q8r.proto.asset.AssetProtos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("import")
public class Importer implements CommandLineRunner {

    private static final String REPLY_QUEUE = "xcurator.import.start";
    private static String correlationId;
    private final ExpoDBClient expoDBClient;
    private final APXMLClient APClient;

    private final APSparqlClient apSparqlClient;
    private final AssetService assetService;
    private final ImporterProperties properties;

    private final DeleteMuseumObjectCommand deleteMuseumObjectCommand;
    private static boolean isImporting = false;

    public Importer(ExpoDBClient expoDBClient, APXMLClient APClient, APSparqlClient apSparqlClient, AssetService assetService, ImporterProperties properties, DeleteMuseumObjectCommand deleteMuseumObjectCommand) {
        this.expoDBClient = expoDBClient;
        this.APClient = APClient;
        this.apSparqlClient = apSparqlClient;
        this.assetService = assetService;
        this.properties = properties;
        this.deleteMuseumObjectCommand = deleteMuseumObjectCommand;
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.properties.isEnabled()) {
            correlationId = UUID
                    .randomUUID()
                    .toString();
            log.info("Importer is starting with id " + correlationId);
            assetService.initImportSequence(REPLY_QUEUE, correlationId);
            Thread.sleep(10000);
            if(!isImporting) {
                log.info("Got no ack in time, start anyway importing");
                importData();
            }
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = REPLY_QUEUE, durable = "true",
            arguments = @Argument(name = "x-dead-letter-exchange", value = "assets-dlx")),
            exchange = @Exchange(value = "assets", type = "topic"), key = REPLY_QUEUE))
    void importImages(AssetProtos.AssetStoreInitAckEventProto protoList, Message message) {
        var messageCorrelation =  message.getMessageProperties().getCorrelationId();
        log.info("Got acknoledge of correlationId (" + messageCorrelation + "). Start import: " + correlationId.equals(messageCorrelation));
        triggerImportRequest(protoList, messageCorrelation);
    }

    private void triggerImportRequest(AssetProtos.AssetStoreInitAckEventProto eventProto, String messageCorrelationId) {
        if (!eventProto
                .getTypeList()
                .contains(AssetProtos.AssetTypeProto.IMAGE)) {
            log.warn("Got a ack with a wrong asset type. Ignore message!");
            throw new AmqpRejectAndDontRequeueException("Got a ack with a wrong asset type. Ignore message!");
        }  if (!correlationId.equals(messageCorrelationId)) {
            log.warn("Got a ack with a different projectId and correlationId. Ignore message!");
            throw new AmqpRejectAndDontRequeueException("Got a ack with a different projectId and correlationId. Ignore message. Ignore Message!");
        }

        log.info("Got import sequence ack. start import sequence.");

        if(!isImporting)
            importData();
    }

    private void importData(){
        isImporting = true;
        int imported = 0;
        try {
//            deleteMuseumObjectCommand.deleteAll();
            imported += expoDBClient.importObjects();
//            imported += APClient.importObjects(1000);
            imported += apSparqlClient.importObjects();

            log.info("Done importing: " + imported + " museum objects");
            log.info( "Import sequence finished. Imported " + imported + " artifacts.");
        } catch (Exception e) {
            log.warn("Unable to get xcurator data. Reason: " + e.getMessage());
            log.warn("Try again later!");
        }
    }
}


