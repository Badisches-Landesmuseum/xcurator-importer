package de.dreipc.xcurator.xcuratorimportservice.translation;

import de.dreipc.xcurator.xcuratorimportservice.commands.CreateTextContentCommand;
import de.dreipc.xcurator.xcuratorimportservice.config.DeeplProperties;
import de.dreipc.xcurator.xcuratorimportservice.config.TranslatorConfig;
import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.testutil.EntityUtil;
import de.dreipc.xcurator.xcuratorimportservice.testutil.UnitTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {CreateTextContentCommand.class, TranslatorConfig.class, DeeplProperties.class, Translator.class})
@Import(UnitTestConfiguration.class)
@EnableConfigurationProperties
@Disabled("due to deepL costs")
class CreateTextContentCommandTest {
    private static CreateTextContentCommand createTextContentCommand;
    private static TextContentRepository textContentRepository;

    private static void translateTextContent(String content, LanguageCode sourceLanguage, LanguageCode targetLanguage, String output) throws TranslationException {
        var text = EntityUtil.createTextContent(content, sourceLanguage);
        textContentRepository.insert(text);
        var translatedContent = createTextContentCommand.translateAndCreate(text.getProjectId(),
                text.getSourceId(),
                text.getContent(),
                text.getTextType(),
                targetLanguage).get();

        assertNotNull(translatedContent.getContent().equals(output));
        assertFalse(translatedContent.getOriginalText());
        assertEquals(translatedContent.getLanguageCode(), targetLanguage);
    }

    @BeforeEach
    void setUp(@Autowired Deepl _deepL, @Autowired TextContentRepository _textContentRepository) {
        textContentRepository = _textContentRepository;
        createTextContentCommand = new CreateTextContentCommand(_deepL, _textContentRepository);
    }

    @Test
    void translateGerman() throws TranslationException {
        var content = "Jadestein zur Herstellung einer Petschaft";
        var output = "Jade stone for making a petschaft";
        translateTextContent(content, LanguageCode.de, LanguageCode.en, output);
    }

    @Test
    void translateDutch() throws TranslationException {
        var content = "Vaas in de vorm van een zittende jongen";
        var output = "Vase in the shape of a seated boy";
        translateTextContent(content, LanguageCode.nl, LanguageCode.en, output);
    }


}
