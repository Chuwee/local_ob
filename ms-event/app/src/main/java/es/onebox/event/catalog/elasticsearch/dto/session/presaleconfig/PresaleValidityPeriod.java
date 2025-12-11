package es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig;

import es.onebox.event.catalog.elasticsearch.enums.PeriodRangeType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PresaleValidityPeriod implements Serializable {

    @Serial
    private static final long serialVersionUID = 6236407122001814721L;

    private ZonedDateTime from;
    private ZonedDateTime to;
    private PeriodRangeType type;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public PeriodRangeType getType() {
        return type;
    }

    public void setType(PeriodRangeType type) {
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
