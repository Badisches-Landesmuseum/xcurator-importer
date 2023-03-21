package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs.elasticsearch;

import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndex;
import de.dreipc.xcurator.xcuratorimportservice.elasticserach.ArtifactIndexRepository;
import de.dreipc.xcurator.xcuratorimportservice.models.MuseumObjectCountConnection;
import dreipc.graphql.types.MuseumObjectSearchOrderByInput;
import dreipc.graphql.types.MuseumObjectSearchWhereInput;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class TestArtifactIndexRepositoryStub implements ArtifactIndexRepository {
    @Override
    public Page<ArtifactIndex> searchSimilar(ArtifactIndex entity, String[] fields, Pageable pageable) {
        return null;
    }

    @Override
    public Iterable<ArtifactIndex> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ArtifactIndex> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ArtifactIndex> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ArtifactIndex> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ArtifactIndex> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<ArtifactIndex> findAll() {
        return null;
    }

    @Override
    public Iterable<ArtifactIndex> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(ArtifactIndex entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends ArtifactIndex> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public MuseumObjectCountConnection<ArtifactIndex> searchMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment) {
        return null;
    }

    @Override
    public MuseumObjectCountConnection<ArtifactIndex> exceptionalMuseumObjects(MuseumObjectSearchWhereInput where, List<MuseumObjectSearchOrderByInput> orderBy, int first, int skip, DataFetchingEnvironment environment) {
        return null;
    }


}
