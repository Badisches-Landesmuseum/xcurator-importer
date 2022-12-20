package de.dreipc.xcurator.xcuratorimportservice.namedentities;

import dreipc.q8r.proto.asset.document.NamedEntitiesProtos;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NamedEntityParser {

    public NamedEntity parse(NamedEntitiesProtos.NamedEntityProto namedEntityProto, ObjectId museumObjectId, ObjectId projectId) {
        return NamedEntity.builder()
                .id(new ObjectId())
                .literal(namedEntityProto.getLiteral())
                .type(namedEntityProto.getType())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .knowledgeBaseId(namedEntityProto.getKnowledgeBaseId())
                .startPosition(namedEntityProto.getStartPosition())
                .projectId(projectId)
                .endPosition(namedEntityProto.getEndPosition())
                .museumObjectId(museumObjectId)
                .sourceId(new ObjectId(namedEntityProto.getSourceId()))
                .build();
    }


}
