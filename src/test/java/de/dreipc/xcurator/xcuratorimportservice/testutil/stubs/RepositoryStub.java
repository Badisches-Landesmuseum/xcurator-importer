package de.dreipc.xcurator.xcuratorimportservice.testutil.stubs;

import de.dreipc.xcurator.xcuratorimportservice.models.IdentifiedObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RepositoryStub<T extends IdentifiedObject> implements MongoRepository<T, ObjectId> {

    protected final Map<ObjectId, T> memoryStorage = new HashMap<>();

    @Override
    public <S extends T> S save(S entity) {
        var id = entity.getId() == null ? new ObjectId() : entity.getId();
        entity.setId(id);
        memoryStorage.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(ObjectId id) {
        var source = memoryStorage.get(id);
        if (source == null) return Optional.empty();
        else return Optional.of(source);

    }

    @Override
    public boolean existsById(ObjectId id) {
        return memoryStorage.containsKey(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(memoryStorage.values());
    }

    @Override
    public Iterable<T> findAllById(Iterable<ObjectId> ids) {
        Objects.requireNonNull(ids);
        return StreamSupport.stream(ids.spliterator(), false)
                .filter(memoryStorage::containsKey)
                .map(memoryStorage::get)
                .collect(Collectors.toList());

    }

    @Override
    public long count() {
        return memoryStorage.size();
    }

    @Override
    public void deleteById(ObjectId id) {
        memoryStorage.remove(id);
    }

    @Override
    public void delete(T entity) {
        memoryStorage.values().remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends ObjectId> ids) {
        ids.forEach(memoryStorage::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Collection<T> values = memoryStorage.values();
        entities.forEach(values::remove);
    }

    @Override
    public void deleteAll() {
        memoryStorage.clear();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return Collections.emptyList();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends T> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends T> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return null;
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return memoryStorage.size();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


}
