package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import de.dreipc.xcurator.xcuratorimportservice.repositories.NamedEntityRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class TextContentDataFetcher {


    private final NamedEntityRepository namedEntityRepository;

    public TextContentDataFetcher(NamedEntityRepository namedEntityRepository) {
        this.namedEntityRepository = namedEntityRepository;
    }


    @DgsData(parentType = "TextContent")
    public CompletableFuture<List<NamedEntity>> entities(@NotNull DgsDataFetchingEnvironment dfe, DgsDataFetchingEnvironment environment) {
        var source = (TextContent) dfe.getSource();
        var entities = namedEntityRepository.findAllBySourceId(source.getId());
        if (entities.size() == 0 | entities == null)
            return CompletableFuture.supplyAsync(() -> new ArrayList<>());
        return CompletableFuture.supplyAsync(() -> entities);
    }
}
