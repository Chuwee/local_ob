package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;

@MaxLimit(1000)
@DefaultLimit(50)
public class SeasonTicketRenewalsFilter extends SeasonTicketRenewalFilter {
    private static final long serialVersionUID = 1L;

    @JsonProperty("season_ticket_id")
    private Integer seasonTicketId;

    public Integer getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Integer seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }
}
