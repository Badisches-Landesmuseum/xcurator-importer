package de.dreipc.xcurator.xcuratorimportservice.translation;

import com.deepl.api.DeepLException;

public class TranslationException  extends Exception {
    public TranslationException(Exception e) {
        super(e);
    }
}
