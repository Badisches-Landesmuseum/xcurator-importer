package de.dreipc.xcurator.xcuratorimportservice.elasticserach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Tolerate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Document(indexName = "xcurator-service.artifacts")
@Builder
@Component
public class ArtifactIndex {

    @Id
    @NonNull
    @Field(fielddata = true, type = FieldType.Keyword)
    String id;
    @Field(type = FieldType.Text)
    List<String> titles;
    @Field(type = FieldType.Text)
    List<String> descriptions;
    @Field(type = FieldType.Keyword)
    List<String> keywords;
    @Field(type = FieldType.Keyword)
    List<String> materials;
    @Field(type = FieldType.Date)
    Instant updatedAt;
    @Field(type = FieldType.Date)
    Instant createdAt;
    @Field(type = FieldType.Date)
    Instant earliestDate;
    @Field(type = FieldType.Date)
    Instant latestDate;
    @Field(type = FieldType.Keyword)
    String locationName;
    @Builder.Default
    @Field(type = FieldType.Keyword)
    List<String> topics = new ArrayList<>();
    @Field(type = FieldType.Keyword)
    String countryName;
    @Field(type = FieldType.Keyword)
    String epoch;
    String dataSource;

    @Tolerate
    public ArtifactIndex() {
    }

}
