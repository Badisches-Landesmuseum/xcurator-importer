package de.dreipc.xcurator.xcuratorimportservice.exceptions;

public class ExpoDBClientException extends RuntimeException {
    public ExpoDBClientException() {
    }

    public ExpoDBClientException(String message) {
        super(message);
    }

    public ExpoDBClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpoDBClientException(Throwable cause) {
        super(cause);
    }

    public ExpoDBClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
