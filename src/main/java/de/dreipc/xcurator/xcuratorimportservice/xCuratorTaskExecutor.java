package de.dreipc.xcurator.xcuratorimportservice;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("importer")
public class xCuratorTaskExecutor implements ApplicationRunner {

    private final Importer importer;

    public xCuratorTaskExecutor(Importer importer) {
        this.importer = importer;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        importer.run();
    }
}
