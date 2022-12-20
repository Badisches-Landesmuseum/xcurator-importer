package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import org.bson.types.ObjectId;

import java.util.List;

public interface TextContentRepositoryCustom {

    List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds);

    List<TextContent> findAllBySourceId(ObjectId sourceId);

    List<ObjectId> findAllIdsBySourceIdAndTextType(ObjectId sourceId, TextType textType);

    void deleteAllBySourceId(List<ObjectId> museumObjectIds);


}
