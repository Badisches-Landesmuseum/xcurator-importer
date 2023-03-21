package de.dreipc.xcurator.xcuratorimportservice.commands;

import de.dreipc.xcurator.xcuratorimportservice.models.LanguageCode;
import de.dreipc.xcurator.xcuratorimportservice.models.TextContent;
import de.dreipc.xcurator.xcuratorimportservice.models.TextType;
import de.dreipc.xcurator.xcuratorimportservice.repositories.TextContentRepository;
import de.dreipc.xcurator.xcuratorimportservice.translation.Translator;
import dreipc.common.graphql.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class CreateTextContentCommand {

    private final Translator translator;
    private final TextContentRepository textContentRepository;

    public CreateTextContentCommand(Translator translator, TextContentRepository textContentRepository) {
        this.translator = translator;
        this.textContentRepository = textContentRepository;
    }

    public Optional<TextContent> translateAndCreate(
            ObjectId projectId,
            ObjectId sourceId,
            String sourceContent,
            TextType textType,
            LanguageCode targetLanguage
    ) {

        try {
            if (textContentRepository.existsByTextTypeAndSourceIdAndLanguageCode(
                    textType,
                    sourceId,
                    targetLanguage)) throw new DuplicateException("text translation is already exists");

            var translated = translator.translate(sourceContent, targetLanguage.getLocal());

            TextContent textContent = TextContent
                    .builder()
                    .id(new ObjectId())
                    .originalText(false)
                    .content(translated)
                    .textType(textType)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .projectId(projectId)
                    .sourceId(sourceId)
                    .languageCode(targetLanguage)
                    .build();

            return Optional.of(textContent);

        } catch (Exception e) {
            log.error("Could not add object:", e);
            return Optional.empty();
        }

    }


}
