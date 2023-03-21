package de.dreipc.xcurator.xcuratorimportservice.graphql.queries;

import com.netflix.graphql.dgs.DgsDataLoader;
import de.dreipc.xcurator.xcuratorimportservice.wikipedia.WikiPedia;
import de.dreipc.xcurator.xcuratorimportservice.wikipedia.WikiPediaBaseEntity;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.BatchLoaderWithContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static java.util.stream.Collectors.groupingBy;

@DgsDataLoader(name = "wikiPedia")
@RequiredArgsConstructor
public class WikiPediaLoader implements BatchLoaderWithContext<String, WikiPediaBaseEntity> {

    private final WikiPedia wikiPedia;
    private final Executor executor;

    @Override
    public CompletionStage<List<WikiPediaBaseEntity>> load(List<String> titles, BatchLoaderEnvironment environment) {
        return CompletableFuture.supplyAsync(() ->
                environment.getKeyContexts()
                        .entrySet().stream().collect(groupingBy(Map.Entry::getValue))
                        .entrySet().stream().map(elem -> {
                            var language = elem.getKey().toString();
                            var titleIds = elem.getValue().stream().map(entry -> entry.getKey().toString()).toList();

                            return wikiPedia.entities(titleIds, language);
                        }).flatMap(List::stream)
                        .toList(), executor);
    }
}
