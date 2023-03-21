package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import org.bson.types.ObjectId;

import java.util.List;

public interface TextContentRepositoryCustom {

    List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds);

    List<TextContent> findAllBySourceId(ObjectId sourceId);

    List<ObjectId> findAllIdsByLanguageCode(LanguageCode languageCode);

    void deleteAllBySourceId(List<ObjectId> museumObjectIds);


}
