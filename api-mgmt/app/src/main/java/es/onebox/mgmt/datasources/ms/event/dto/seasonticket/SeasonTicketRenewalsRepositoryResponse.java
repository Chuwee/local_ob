package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketRenewalsRepositoryResponse extends BaseResponseCollection<SeasonTicketRenewalSeat, Metadata> {
    private static final long serialVersionUID = 1L;

    private SeasonTicketRenewalSeatsSummary summary;

    public SeasonTicketRenewalSeatsSummary getSummary() {
        return summary;
    }

    public void setSummary(SeasonTicketRenewalSeatsSummary summary) {
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