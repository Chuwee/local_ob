package es.onebox.common.datasources.ms.event.dto;

import java.io.Serial;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EventRateDTO extends RateDTO {

    @Serial
    private static final long serialVersionUID = -3572359705249025047L;

    private RateGroupResponseDTO rateGroup;

    public EventRateDTO() {
    }

    public EventRateDTO(Long id, String name, String description, Boolean restrictive, Boolean defaultRate, Map<String, String> translations, RateGroupResponseDTO rateGroup) {
        super(id,name,description, restrictive,defaultRate,translations);
        this.rateGroup = rateGroup;
    }

    public RateGroupResponseDTO getRateGroup() {
        return rateGroup;
    }

    public void setRateGroup(RateGroupResponseDTO rateGroup) {
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
