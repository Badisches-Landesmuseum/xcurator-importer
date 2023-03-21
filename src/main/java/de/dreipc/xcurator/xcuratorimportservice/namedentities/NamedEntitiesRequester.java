package de.dreipc.xcurator.xcuratorimportservice.namedentities;

import de.dreipc.rabbitmq.ProtoPublisher;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import dreipc.q8r.proto.asset.document.NamedEntitiesProtos;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NamedEntitiesRequester {

    private final ProtoPublisher protoPublisher;


    public NamedEntitiesRequester(ProtoPublisher protoPublisher) {
        this.protoPublisher = protoPublisher;
    }


    public void execute(List<TextContent> textContents) {
        textContents.forEach(this::execute);
    }

    public void execute(Iterable<TextContent> textContents) {
        textContents.forEach(this::execute);
    }

    public void execute(TextContent textContent) {
        execute(textContent.getId(), textContent.getContent());
    }

    public void execute(ObjectId id, String content) {
        var proto = NamedEntitiesProtos.NamedEntitiesDetectionAction.newBuilder()
                .setId(id.toString())
                .setContent(content)
                .setModel(NamedEntitiesProtos.ModelTypeProto.GENERAL)
                .build();
        protoPublisher.sendEvent("entities.analyse.text", proto);


    }


}
