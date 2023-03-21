package de.dreipc.xcurator.xcuratorimportservice.graphql.dataFetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndexRepository;
import de.dreipc.xcurator.xcuratorimportservice.namedentities.NamedEntity;
import de.dreipc.xcurator.xcuratorimportservice.repositories.MuseumObjectRepository;
import de.dreipc.xcurator.xcuratorimportservice.services.MaterialDistributionService;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

@DgsComponent
public class EntitiesDataFetcher {


    private final ArtifactIndexRepository artifactIndexRepository;
    private final MuseumObjectRepository museumObjectRepository;

    private final MaterialDistributionService materialDistributionService;


    public EntitiesDataFetcher(ArtifactIndexRepository artifactIndexRepository, MuseumObjectRepository museumObjectRepository, MaterialDistributionService materialDistributionService) {

        this.artifactIndexRepository = artifactIndexRepository;
        this.museumObjectRepository = museumObjectRepository;

        this.materialDistributionService = materialDistributionService;
    }

    @DgsData(parentType = "NamedEntity", field = "knowledgeBaseUrl")
    public URL museumImages(@NotNull DgsDataFetchingEnvironment env) {
        var source = (NamedEntity) env.getSource();
        source.getKnowledgeBaseId();
        try {
            return new URL("www.wikidata.org/wiki/" + source.getKnowledgeBaseId());
        } catch (Exception e) {
            return null;
        }


    }

}
