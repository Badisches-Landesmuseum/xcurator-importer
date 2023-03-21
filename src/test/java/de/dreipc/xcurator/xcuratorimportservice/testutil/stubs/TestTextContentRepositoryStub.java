package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TestTextContentRepositoryStub extends RepositoryStub<TextContent> implements TextContentRepository {


    public List<TextContent> findAllBySourceId(ObjectId sourceId) {
        return this.memoryStorage.values()
                .stream().filter(object -> object.getSourceId()
                        .equals(sourceId)).toList();
    }

    @Override
    public List<ObjectId> findAllIdsByLanguageCode(LanguageCode languageCode) {
        throw new NotImplementedException("Not implemented for testing");
    }

    @Override
    public List<ObjectId> findAllIdsBySourceIds(List<ObjectId> sourceIds) {

        return sourceIds.stream().map(this::findAllBySourceId)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .map(TextContent::getId)
                .toList();
    }


    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        this.memoryStorage.values().stream()
                .filter(object -> museumObjectIds.contains(object.getSourceId()))
                .map(TextContent::getId)
                .map(memoryStorage::remove);
    }

    @Override
    public List<TextContent> findTextContentBySourceIdAndTextType(ObjectId sourceId, TextType textType) {
        return this.memoryStorage.values().stream().filter(textContent ->
                textContent.getSourceId().equals(sourceId)
                        && textContent.getTextType().equals(textType)).toList();
    }

    @Override
    public Boolean existsByTextTypeAndSourceIdAndLanguageCode(TextType textType, ObjectId sourceId, LanguageCode languageCode) {
        return !this.memoryStorage.values().stream().filter(textContent ->
                        textContent.getTextType().equals(textType)
                                && textContent.getSourceId().equals(sourceId)
                                && textContent.getLanguageCode().equals(languageCode)
                ).toList()
                .isEmpty();
    }

    @Override
    public Optional<TextContent> findTextContentByTextTypeAndSourceIdAndLanguageCode(TextType textType, ObjectId sourceId, LanguageCode languageCode) {
        throw new NotImplementedException("Not implemented for testing");
    }

    @Override
    public Optional<TextContent> findTextContentByTextTypeAndSourceIdAndOriginalText(TextType textType, ObjectId sourceId, Boolean originalText) {
        throw new NotImplementedException("Not implemented for testing");
    }

    @Override
    public Iterable<TextContent> findAllByLanguageCode(LanguageCode languageCode) {
        return this.memoryStorage.values().stream().filter(textContent -> languageCode.equals(textContent.getLanguageCode())).toList();
    }
}
