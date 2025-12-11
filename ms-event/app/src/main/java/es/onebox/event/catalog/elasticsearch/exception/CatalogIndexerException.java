package es.onebox.event.catalog.elasticsearch.exception;

import java.io.Serial;

public class CatalogIndexerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5341509886747505916L;

    public CatalogIndexerException() {
    }

    public CatalogIndexerException(String message) {
        super(message);
    }

    public CatalogIndexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CatalogIndexerException(Throwable cause) {
        super(cause);
    }

}
