package es.onebox.mgmt.seasontickets.dto.renewals;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketRenewalsResponse extends BaseResponseCollection<SeasonTicketRenewalSeatDTO, Metadata> {
    private static final long serialVersionUID = 1L;

    private SeasonTicketRenewalSeatsSummaryDTO summary;

    public SeasonTicketRenewalSeatsSummaryDTO getSummary() {
        return summary;
    }

    public void setSummary(SeasonTicketRenewalSeatsSummaryDTO summary) {
        this.summary = summary;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
