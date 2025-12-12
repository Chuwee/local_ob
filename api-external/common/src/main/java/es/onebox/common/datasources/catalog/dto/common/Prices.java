package es.onebox.common.datasources.catalog.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Prices implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Price min;
    @JsonProperty("min_promoted")
    private Price minPromoted;
    private Price max;

    public Price getMin() {
        return min;
    }

    public void setMin(Price min) {
        this.min = min;
    }

    public Price getMinPromoted() {
        return minPromoted;
    }

    public void setMinPromoted(Price minPromoted) {
        this.minPromoted = minPromoted;
    }

    public Price getMax() {
        return max;
    }

    public void setMax(Price max) {
        this.max = max;
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
