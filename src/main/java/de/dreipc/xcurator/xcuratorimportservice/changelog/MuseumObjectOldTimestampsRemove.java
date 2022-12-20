package de.dreipc.xcurator.xcuratorimportservice.changelog;

import com.mongodb.BasicDBObject;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@ChangeUnit(id = "MuseumObjectOldTimestampsRemove", order = "1", author = "nbabai")
public class MuseumObjectOldTimestampsRemove {

    private final MongoTemplate mongoTemplate;

    private final String COLLECTION_NAME = "xcurator-museum-objects";

    public MuseumObjectOldTimestampsRemove(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Execution
    public void execute(MongoTemplate mongoTemplate) {
        var searchQuery = new BasicDBObject();

        var updateQuery = new BasicDBObject();
        updateQuery.append("$unset", new BasicDBObject()
                .append("latestDate", ""));
        var resultSet = mongoTemplate.getCollection(COLLECTION_NAME).updateMany(searchQuery, updateQuery);
        log.info(String.format("%s items deleted with old type latest date", resultSet.getModifiedCount()));

        updateQuery = new BasicDBObject();
        updateQuery.append("$unset", new BasicDBObject()
                .append("earliestDate", ""));
        resultSet = mongoTemplate.getCollection(COLLECTION_NAME).updateMany(searchQuery, updateQuery);
        log.info(String.format("%s items deleted with old type earliest date", resultSet.getModifiedCount()));
    }


    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // empty
    }
}
