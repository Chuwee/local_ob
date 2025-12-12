package es.onebox.ms.notification.webhooks.dto.promotion;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionRangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double from;
    private Double to;
    private Double value;
    private Long currencyId;

    public PromotionRangeDTO() {
    }

    public PromotionRangeDTO(Double from, Double to, Double value, Long currencyId) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.currencyId = currencyId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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
