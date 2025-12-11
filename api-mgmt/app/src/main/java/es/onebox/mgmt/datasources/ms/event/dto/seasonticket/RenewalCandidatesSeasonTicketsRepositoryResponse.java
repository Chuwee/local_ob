package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.util.List;

public class RenewalCandidatesSeasonTicketsRepositoryResponse implements Serializable {

    private static final long serialVersionUID = -2289313175282688769L;

    private List<RenewalCandidatesSeasonTicket> seasonTicketRenewalCandidatesList;

    public RenewalCandidatesSeasonTicketsRepositoryResponse() {
    }

    public RenewalCandidatesSeasonTicketsRepositoryResponse(List<RenewalCandidatesSeasonTicket> seasonTicketRenewalCandidatesList) {
        this.seasonTicketRenewalCandidatesList = seasonTicketRenewalCandidatesList;
    }

    public List<RenewalCandidatesSeasonTicket> getSeasonTicketRenewalCandidatesList() {
        return seasonTicketRenewalCandidatesList;
    }

    public void setSeasonTicketRenewalCandidatesList(List<RenewalCandidatesSeasonTicket> seasonTicketRenewalCandidatesList) {
        this.seasonTicketRenewalCandidatesList = seasonTicketRenewalCandidatesList;
    }
}
