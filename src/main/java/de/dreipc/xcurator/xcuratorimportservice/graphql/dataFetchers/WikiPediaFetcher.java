package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import de.dreipc.xcurator.xcuratorimportservice.graphql.queries.WikiPediaLoader;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiDataBaseEntity;
import dreipc.graphql.types.WikiPediaEntitiesInput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@DgsComponent
@RequiredArgsConstructor
public class WikiPediaFetcher {

    @DgsQuery
    public CompletableFuture<List<WikiDataBaseEntity>> wikipediaEntities(@InputArgument WikiPediaEntitiesInput where, @NonNull DgsDataFetchingEnvironment env) {
        if (where.getTitles().isEmpty())
            throw new IllegalArgumentException("Ids are empty, please add WikiData Q ids to fetch entities.");

        List<Object> language = where
                .getTitles()
                .stream()
                .map(id -> where.getLanguage().name().toLowerCase())
                .collect(Collectors.toList());
        var loader = env.<String, WikiDataBaseEntity>getDataLoader(WikiPediaLoader.class);
        return loader.loadMany(where.getTitles(), language);
    }
}
