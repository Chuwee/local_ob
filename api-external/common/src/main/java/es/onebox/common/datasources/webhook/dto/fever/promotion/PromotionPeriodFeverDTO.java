package es.onebox.common.datasources.webhook.dto.fever.promotion;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.promotion.enums.PromotionValidityType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class PromotionPeriodFeverDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PromotionValidityType type;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public PromotionValidityType getType() {
        return type;
    }

    public void setType(PromotionValidityType type) {
        this.type = type;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
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
