package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLinkSeatResult;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketLinkResponse implements Serializable {
    private static final long serialVersionUID = -3905379753443517996L;

    private List<SeasonTicketLinkSeatResult> results;

    public List<SeasonTicketLinkSeatResult> getResults() {
        return results;
    }

    public void setResults(List<SeasonTicketLinkSeatResult> results) {
        this.results = results;
    }
}
