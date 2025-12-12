package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.promotion.enums.PromotionDiscountType;
import java.io.Serial;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class PromotionDiscountConfigFeverDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private PromotionDiscountType type;
    private Double value;

    public PromotionDiscountType getType() {
        return type;
    }

    public void setType(PromotionDiscountType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
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
