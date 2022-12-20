package de.dreipc.xcurator.xcuratorimportservice.translation;

import java.util.Locale;

public interface Translator {

    String translate(String text, Locale language) throws TranslationException;
    String translate(String text, Locale language, Locale sourceLanguage) throws TranslationException;
}
