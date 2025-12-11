package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import es.onebox.elasticsearch.annotation.ElasticRepository;
import es.onebox.event.catalog.elasticsearch.dto.BaseEventData;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

@ElasticRepository(indexName = EventDataUtils.EVENT_INDEX, queryLimit = 500000)
public class SeasonTicketData extends BaseEventData {

    @Serial
    private static final long serialVersionUID = 1L;

    private SeasonTicket seasonTicket;

    public SeasonTicket getSeasonTicket() {
        return seasonTicket;
    }

    public void setSeasonTicket(SeasonTicket seasonTicket) {
        this.seasonTicket = seasonTicket;
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
