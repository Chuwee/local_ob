package es.onebox.fifaqatar.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidSignatureException extends FifaQatarBaseException {

    public InvalidSignatureException() {
        super();
    }

    public InvalidSignatureException(String message) {
        super(message);
    }
}
