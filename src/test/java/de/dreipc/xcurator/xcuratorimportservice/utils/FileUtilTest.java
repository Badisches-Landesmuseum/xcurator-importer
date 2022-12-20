package de.dreipc.xcurator.xcuratorimportservice.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FileUtilTest {

    @Test
    void getResourceFileAsString() {
        var applicationYmlContent = FileUtil.getResourceFileAsString("application.yml");
        assertThat(applicationYmlContent).contains("spring:");
    }
}