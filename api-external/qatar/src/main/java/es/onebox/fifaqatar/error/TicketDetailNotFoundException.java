package es.onebox.fifaqatar.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TicketDetailNotFoundException extends FifaQatarBaseException {

    public TicketDetailNotFoundException() {
        super();
    }

    public TicketDetailNotFoundException(String message) {
        super(message);
    }
}
