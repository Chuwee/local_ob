package es.onebox.mgmt.seasontickets.dto;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketLinkResponseDTO implements Serializable {
    private static final long serialVersionUID = -3905379753443517996L;

    private List<SeasonTicketLinkSeatResultDTO> results;

    public List<SeasonTicketLinkSeatResultDTO> getResults() {
        return results;
    }

    public void setResults(List<SeasonTicketLinkSeatResultDTO> results) {
        this.results = results;
    }
}
