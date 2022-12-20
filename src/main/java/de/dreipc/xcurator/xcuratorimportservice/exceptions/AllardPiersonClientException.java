package de.dreipc.xcurator.xcuratorimportservice.exceptions;

public class AllardPiersonClientException extends RuntimeException {
    public AllardPiersonClientException() {
    }

    public AllardPiersonClientException(String message) {
        super(message);
    }

    public AllardPiersonClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public AllardPiersonClientException(Throwable cause) {
        super(cause);
    }

    public AllardPiersonClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
