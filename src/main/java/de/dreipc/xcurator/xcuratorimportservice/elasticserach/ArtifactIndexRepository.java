package de.dreipc.xcurator.xcuratorimportservice.elasticserach;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactIndexRepository extends ElasticsearchRepository<ArtifactIndex, String>, ArtifactIndexRepositoryCustom {

}
