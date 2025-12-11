package es.onebox.event.seasontickets.dto.renewals;

import java.io.Serializable;
import java.util.List;

public class RenewalCandidatesSeasonTicketsResponse implements Serializable {
    private static final long serialVersionUID = -3926771211773832205L;

    private List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidatesList;

    public RenewalCandidatesSeasonTicketsResponse(List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidatesList) {
        this.seasonTicketRenewalCandidatesList = seasonTicketRenewalCandidatesList;
    }

    public List<RenewalCandidateSeasonTicketDTO> getSeasonTicketRenewalCandidatesList() {
        return seasonTicketRenewalCandidatesList;
    }

    public void setSeasonTicketRenewalCandidatesList(List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidatesList) {
        this.seasonTicketRenewalCandidatesList = seasonTicketRenewalCandidatesList;
    }
}
