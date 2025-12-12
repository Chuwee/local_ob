package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import java.io.Serial;
import java.io.Serializable;

public class TicketReleaseCondition implements Serializable {

    @Serial
    private static final long serialVersionUID = 7770763214687187986L;

    private String message;
    private ReleaseConditionAction action;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReleaseConditionAction getAction() {
        return action;
    }

    public void setAction(ReleaseConditionAction action) {
        this.action = action;
    }
}
