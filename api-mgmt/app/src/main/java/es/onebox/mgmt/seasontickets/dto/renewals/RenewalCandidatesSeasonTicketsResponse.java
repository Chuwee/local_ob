package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RenewalCandidatesSeasonTicketsResponse implements Serializable {

    private static final long serialVersionUID = -2867189255507787515L;

    @JsonProperty("data")
    private List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidateList;

    public RenewalCandidatesSeasonTicketsResponse(List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidateList) {
        this.seasonTicketRenewalCandidateList = seasonTicketRenewalCandidateList;
    }

    public List<RenewalCandidateSeasonTicketDTO> getSeasonTicketRenewalCandidateList() {
        return seasonTicketRenewalCandidateList;
    }

    public void setSeasonTicketRenewalCandidateList(List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCandidateList) {
        this.seasonTicketRenewalCandidateList = seasonTicketRenewalCandidateList;
    }
}
