package es.onebox.common.datasources.catalog.dto.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class PromotionValidityPeriod implements Serializable {

    private static final long serialVersionUID = 4594121586414848459L;

    private PromotionValidityPeriodType type;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public PromotionValidityPeriodType getType() {
        return type;
    }

    public void setType(PromotionValidityPeriodType type) {
        this.type = type;
    }

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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
