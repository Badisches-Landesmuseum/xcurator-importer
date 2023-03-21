package de.dreipc.xcurator.xcuratorimportservice.config;

import de.dreipc.xcurator.xcuratorimportservice.translation.Deepl;
import de.dreipc.xcurator.xcuratorimportservice.translation.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TranslatorConfig {

    @Bean
    @Primary
    public Translator translator(@Autowired DeeplProperties _properties) {
        return new Deepl(_properties.getAuthKey());
    }


}
