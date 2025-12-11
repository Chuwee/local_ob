package es.onebox.event.seasontickets.dto.renewals;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

public class SeasonTicketRenewalSeatsResponse extends BaseResponseCollection<SeasonTicketRenewalSeat, Metadata> {
    private static final long serialVersionUID = 1L;

    private SeasonTicketRenewalSeatsSummary summary;

    public SeasonTicketRenewalSeatsSummary getSummary() {
        return summary;
    }

    public void setSummary(SeasonTicketRenewalSeatsSummary summary) {
        this.summary = summary;
    }
}
