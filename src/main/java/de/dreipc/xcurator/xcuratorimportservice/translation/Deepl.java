package de.dreipc.xcurator.xcuratorimportservice.translation;


import com.deepl.api.DeepLException;
import org.springframework.lang.NonNull;

import java.util.Locale;

public class Deepl implements Translator {

    private final com.deepl.api.Translator deeplTranslator;

    public Deepl(String apiKey) {
        this.deeplTranslator = new com.deepl.api.Translator(apiKey);
    }

    @Override
    public String translate(@NonNull String text, @NonNull Locale language) throws TranslationException {
        return translate(text, language, null);
    }

    @Override
    public String translate(@NonNull String text, @NonNull Locale language, Locale source) throws TranslationException {
        String sourceLanguage = source != null ? source.getLanguage(): null;
        try {
            var result =  deeplTranslator.translateText(text, sourceLanguage, language.getLanguage());
            return result.getText();
        } catch (DeepLException e) {
            throw new TranslationException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
