package de.dreipc.xcurator.xcuratorimportservice;

import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({UnitTestConfiguration.class})
class xCuratorImportServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
