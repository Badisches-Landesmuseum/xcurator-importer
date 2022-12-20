package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TextContentRepository extends MongoRepository<TextContent, ObjectId>, TextContentRepositoryCustom {

    List<TextContent> findTextContentBySourceIdAndTextType(ObjectId sourceId, TextType textType);


}
