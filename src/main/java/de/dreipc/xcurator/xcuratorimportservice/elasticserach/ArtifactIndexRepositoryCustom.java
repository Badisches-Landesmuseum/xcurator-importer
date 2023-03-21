package de.dreipc.xcurator.xcuratorimportservice.elasticserach;

import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObjectCountConnection;
import dreipc.graphql.types.MuseumObjectSearchOrderByInput;
import dreipc.graphql.types.MuseumObjectSearchWhereInput;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public interface ArtifactIndexRepositoryCustom {

    MuseumObjectCountConnection<ArtifactIndex> searchMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment);

    MuseumObjectCountConnection<ArtifactIndex> exceptionalMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment);


}
