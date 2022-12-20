package de.dreipc.xcurator.xcuratorimportservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class Epoch {
    String name;
    Integer start;
    Integer end;
}
