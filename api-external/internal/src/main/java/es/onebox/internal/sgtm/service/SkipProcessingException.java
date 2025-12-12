package es.onebox.internal.sgtm.service;

/**
 * Exception to indicate that SGTM webhook processing should be skipped and a 204 No Content should be returned.
 */
public class SkipProcessingException extends RuntimeException {
    public SkipProcessingException() {
        super("SGTM webhook processing skipped: channel not allowed");
    }
} 