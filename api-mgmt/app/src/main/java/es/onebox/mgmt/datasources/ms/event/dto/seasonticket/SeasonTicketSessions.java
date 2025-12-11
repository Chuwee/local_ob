package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketSessions extends BaseResponseCollection<SeasonTicketSession, Metadata> {
    private static final long serialVersionUID = -6824773153634146338L;

    SeasonTicketSessionsSummary summary;

    public SeasonTicketSessionsSummary getSummary() {
        return summary;
    }

    public void setSummary(SeasonTicketSessionsSummary summary) {
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
