package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiData;
import de.dreipc.xcurator.xcuratorimportservice.wikidata.WikiDataBaseEntity;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static java.util.stream.Collectors.groupingBy;

@DgsDataLoader(name = "wikiData")
@RequiredArgsConstructor
public class WikiDataLoader implements BatchLoaderWithContext<String, WikiDataBaseEntity> {

    private final WikiData wikidata;
    private final Executor executor;

    @Override
    public CompletionStage<List<WikiDataBaseEntity>> load(List<String> ids, BatchLoaderEnvironment environment) {
        return CompletableFuture.supplyAsync(() ->
                environment.getKeyContexts()
                        .entrySet().stream().collect(groupingBy(Map.Entry::getValue))
                        .entrySet().stream().map(elem -> {
                            var language = elem.getKey().toString();
                            var languageIds = elem.getValue().stream().map(entry -> entry.getKey().toString()).toList();

                            return wikidata.entities(languageIds, language);
                        }).flatMap(List::stream)
                        .toList(), executor);
    }
}
