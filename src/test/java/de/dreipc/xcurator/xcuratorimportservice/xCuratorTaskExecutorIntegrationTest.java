package de.dreipc.xcurator.xcuratorimportservice;

import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import({UnitTestConfiguration.class})
@ActiveProfiles(profiles = {"integrationtest", "importertest"})
class xCuratorTaskExecutorIntegrationTest {

    @SpyBean
    private final xCuratorTaskExecutor applicationRunnerTaskExecutor;

    xCuratorTaskExecutorIntegrationTest(@Autowired xCuratorTaskExecutor applicationRunnerTaskExecutor) {
        this.applicationRunnerTaskExecutor = applicationRunnerTaskExecutor;
    }

    @Test
    void whenContextLoads() throws Exception {
        verify(applicationRunnerTaskExecutor, times(1)).run(any());
    }
}
