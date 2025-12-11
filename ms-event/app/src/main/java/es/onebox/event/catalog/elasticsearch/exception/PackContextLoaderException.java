package es.onebox.event.catalog.elasticsearch.exception;


import es.onebox.event.packs.enums.PackStatus;

import java.io.Serial;

public class PackContextLoaderException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4953903555662778157L;

    private final PackStatus status;

    public PackContextLoaderException(String message, PackStatus status) {
        super(message);
        this.status = status;
    }

    public PackStatus getStatus() {
        return status;
    }
}
