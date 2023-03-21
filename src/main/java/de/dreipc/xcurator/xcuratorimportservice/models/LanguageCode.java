package de.dreipc.xcurator.xcuratorimportservice.models;

import dreipc.graphql.types.Language;

import java.util.Locale;

public enum LanguageCode {
    de,
    en,
    nl;

    public static LanguageCode getLanguageCode(Language language) {
        return switch (language) {
            case EN -> LanguageCode.en;
            case DE -> LanguageCode.de;
            case NL -> LanguageCode.nl;
        };
    }

    public Locale getLocal() {
        return switch (this) {
            case en -> Locale.US;
            case de -> Locale.GERMANY;
            case nl -> new Locale("nl");
        };
    }
}
