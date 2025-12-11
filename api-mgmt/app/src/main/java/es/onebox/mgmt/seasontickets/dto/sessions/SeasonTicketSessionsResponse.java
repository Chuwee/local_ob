package es.onebox.mgmt.seasontickets.dto.sessions;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSessionsSummary;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketSessionsResponse extends BaseResponseCollection<SeasonTicketSessionDTO, Metadata> implements DateConvertible {

    private static final long serialVersionUID = 1L;

    private SeasonTicketSessionsSummary summary;

    @Override
    public void convertDates() {
        if (!CommonUtils.isEmpty(getData())) {
            for (SeasonTicketSessionDTO seasonTicketSession : getData()) {
                seasonTicketSession.convertDates();
            }
        }
    }

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
