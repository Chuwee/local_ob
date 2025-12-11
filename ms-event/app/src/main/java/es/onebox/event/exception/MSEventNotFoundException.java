package es.onebox.event.exception;

import es.onebox.core.exception.OneboxRestException;

public class MSEventNotFoundException extends OneboxRestException {

    private static final long serialVersionUID = 1L;

    public MSEventNotFoundException() {
        super(MsEventErrorCode.EVENT_NOT_FOUND);
    }
}
