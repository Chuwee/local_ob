package es.onebox.mgmt.seasontickets.dto.sessions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SeasonTicketSessionValidationResponse implements Serializable {

    private static final long serialVersionUID = 501953913504502365L;

    private List<SeasonTicketSessionValidation> result = new ArrayList<>();

    public List<SeasonTicketSessionValidation> getResult() {
        return result;
    }

    public void setResult(List<SeasonTicketSessionValidation> result) {
        this.result = result;
    }
}
