package de.dreipc.xcurator.xcuratorimportservice.models;

import java.time.Instant;

public interface TimeStamp {

    Instant getCreatedAt();
    Instant getUpdatedAt();
}
