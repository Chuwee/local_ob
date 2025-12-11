package es.onebox.event.catalog.dto.promotion;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CatalogPromotionValidationPeriodDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CatalogPromotionValidationPeriodType type;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public CatalogPromotionValidationPeriodType getType() {
        return type;
    }

    public void setType(CatalogPromotionValidationPeriodType type) {
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
