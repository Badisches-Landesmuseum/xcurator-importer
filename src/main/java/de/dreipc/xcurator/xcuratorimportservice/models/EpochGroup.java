package de.dreipc.xcurator.xcuratorimportservice.models;


import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@Builder
public class EpochGroup {
    String name;
    Integer count;
    Integer ratio;
    Integer start;
    Integer end;
}
