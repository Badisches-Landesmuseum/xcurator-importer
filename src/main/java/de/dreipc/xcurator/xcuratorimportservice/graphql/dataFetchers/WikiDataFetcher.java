package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.WikiDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiDataBaseEntity;
import dreipc.graphql.types.WikiDataEntitiesInput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@DgsComponent
@RequiredArgsConstructor
public class WikiDataFetcher {

    @DgsQuery
    public CompletableFuture<List<WikiDataBaseEntity>> wikidataEntities(@InputArgument WikiDataEntitiesInput where, @NonNull DgsDataFetchingEnvironment env) {
        if (where.getIds().isEmpty())
            throw new IllegalArgumentException("Ids are empty, please add WikiData Q ids to fetch entities.");

        List<Object> language = where
                .getIds()
                .stream()
                .map(id -> where.getLanguage().name().toLowerCase())
                .collect(Collectors.toList());
        var loader = env.<String, WikiDataBaseEntity>getDataLoader(WikiDataLoader.class);
        return loader.loadMany(where.getIds(), language);
    }
}
