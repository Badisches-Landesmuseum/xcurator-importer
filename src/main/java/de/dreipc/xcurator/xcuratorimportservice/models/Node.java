package de.dreipc.xcurator.xcuratorimportservice.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An object with an ID
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "__typename"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MuseumObject.class, name = "MuseumObject"),
        @JsonSubTypes.Type(value = MuseumImage.class, name = "MuseumImage")

})
public interface Node {
    /**
     * The ID of an object
     */
    String getId();

    /**
     * The ID of an object
     */
    void setId(String id);
}
