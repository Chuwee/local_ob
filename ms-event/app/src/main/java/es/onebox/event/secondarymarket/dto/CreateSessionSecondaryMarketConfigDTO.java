package es.onebox.event.secondarymarket.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateSessionSecondaryMarketConfigDTO extends SecondaryMarketConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SecondaryMarketType type;

    public SecondaryMarketType getType() {
        return type;
    }

    public void setType(SecondaryMarketType type) {
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