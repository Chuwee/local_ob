package es.onebox.mgmt.seasontickets.dto.sessions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SeasonTicketSessionAssignationResponse implements Serializable {

    private static final long serialVersionUID = 4080489945213719264L;

    private List<SeasonTicketSessionAssignation> result = new ArrayList<>();

    public List<SeasonTicketSessionAssignation> getResult() {
        return result;
    }

    public void setResult(List<SeasonTicketSessionAssignation> result) {
        this.result = result;
    }
}
