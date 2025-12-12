package es.onebox.fifaqatar.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public final class InvalidAuthException extends FifaQatarBaseException{

    public InvalidAuthException() {
        super();
    }

    public InvalidAuthException(String message) {
        super(message);
    }
}
