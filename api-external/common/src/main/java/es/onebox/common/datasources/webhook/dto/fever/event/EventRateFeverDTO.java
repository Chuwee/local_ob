package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serial;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonNaming(SnakeCaseStrategy.class)
public class EventRateFeverDTO extends RateFeverDTO {

    @Serial
    private static final long serialVersionUID = -3572359705249025047L;

    private RateGroupResponseFeverDTO rateGroup;

    public EventRateFeverDTO() {
    }

    public EventRateFeverDTO(Long id, String name, String description, Boolean restrictive, Boolean defaultRate, Map<String, String> translations, RateGroupResponseFeverDTO rateGroup) {
        super(id,name,description, restrictive,defaultRate,translations);
        this.rateGroup = rateGroup;
    }

    public RateGroupResponseFeverDTO getRateGroup() {
        return rateGroup;
    }

    public void setRateGroup(RateGroupResponseFeverDTO rateGroup) {
        this.rateGroup = rateGroup;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
