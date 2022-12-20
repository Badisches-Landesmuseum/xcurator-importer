package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class TestTextContentRepositoryStub extends RepositoryStub<TextContent> implements TextContentRepository {


    public List<TextContent> findAllBySourceId(ObjectId sourceId) {
        return this.memoryStorage.values()
                .stream().filter(object -> object.getSourceId()
                        .equals(sourceId)).toList();
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
    public List<ObjectId> findAllIdsBySourceIdAndTextType(ObjectId sourceId, TextType textType) {
        return this.memoryStorage.values().stream()
                .filter(object -> object.getSourceId().equals(sourceId))
                .filter(object -> object.getTextType().equals(textType))
                .map(TextContent::getId)
                .toList();
    }

    @Override
    public void deleteAllBySourceId(List<ObjectId> museumObjectIds) {
        this.memoryStorage.values().stream()
                .filter(object -> museumObjectIds.contains(object.getSourceId()))
                .map(TextContent::getId)
                .map(id -> memoryStorage.remove(id));
    }

    @Override
    public List<TextContent> findTextContentBySourceIdAndTextType(ObjectId sourceId, TextType textType) {
        return null;
    }
}
