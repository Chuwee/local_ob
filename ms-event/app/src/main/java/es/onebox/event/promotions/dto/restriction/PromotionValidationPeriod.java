package es.onebox.event.promotions.dto.restriction;

import es.onebox.event.promotions.enums.PromotionValidationPeriodType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;

public class PromotionValidationPeriod implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionValidationPeriodType type;
    private Date from;
    private Date to;

    public PromotionValidationPeriod() {
    }

    public PromotionValidationPeriod(Date dateFrom, Date dateTo) {
        this.from = dateFrom;
        this.to = dateTo;
    }

    public PromotionValidationPeriodType getType() {
        return type;
    }

    public void setType(PromotionValidationPeriodType type) {
        this.type = type;
    }


    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
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
