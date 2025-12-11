package es.onebox.event.common.amqp.streamprogress.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeasonTicketMessage extends ProgressMessage {

    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private SeasonTicketTypeMessage type;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public SeasonTicketTypeMessage getType() {
        return type;
    }

    public void setType(SeasonTicketTypeMessage type) {
        this.type = type;
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
