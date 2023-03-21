package de.dreipc.xcurator.xcuratorimportservice.repositories;


import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface TextContentRepository extends MongoRepository<TextContent, ObjectId>, TextContentRepositoryCustom {

    List<TextContent> findTextContentBySourceIdAndTextType(ObjectId sourceId, TextType textType);

    Boolean existsByTextTypeAndSourceIdAndLanguageCode(TextType textType, ObjectId sourceId, LanguageCode languageCode);

    Optional<TextContent> findTextContentByTextTypeAndSourceIdAndLanguageCode(TextType textType, ObjectId sourceId, LanguageCode languageCode);

    Optional<TextContent> findTextContentByTextTypeAndSourceIdAndOriginalText(TextType textType, ObjectId sourceId, Boolean originalText);

    Iterable<TextContent> findAllByLanguageCode(LanguageCode languageCode);


}
