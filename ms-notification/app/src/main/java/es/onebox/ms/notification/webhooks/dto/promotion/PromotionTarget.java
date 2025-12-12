package es.onebox.ms.notification.webhooks.dto.promotion;

import es.onebox.ms.notification.webhooks.enums.promotion.PromotionTargetType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public abstract class PromotionTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Type can't be null")
    private PromotionTargetType type;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
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
