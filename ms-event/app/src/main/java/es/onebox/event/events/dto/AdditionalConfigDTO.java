package es.onebox.event.events.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class AdditionalConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long avetPriceId;

    public Long getAvetPriceId() {
        return avetPriceId;
    }

    public void setAvetPriceId(Long avetPriceId) {
        this.avetPriceId = avetPriceId;
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
