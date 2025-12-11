package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.channels.enums.ChannelSurchargesCalculation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SurchargesSettings implements Serializable {

    private static final long serialVersionUID = 8242523547801901228L;

    private ChannelSurchargesCalculation calculation;

    public ChannelSurchargesCalculation getCalculation() {
        return calculation;
    }

    public void setCalculation(ChannelSurchargesCalculation calculation) {
        this.calculation = calculation;
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
