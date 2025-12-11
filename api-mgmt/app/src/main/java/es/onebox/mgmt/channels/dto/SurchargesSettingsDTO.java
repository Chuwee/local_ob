package es.onebox.mgmt.channels.dto;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSurchargesCalculationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SurchargesSettingsDTO implements Serializable {

    private static final long serialVersionUID = 8242523547801901228L;

    private ChannelSurchargesCalculationDTO calculation;

    public ChannelSurchargesCalculationDTO getCalculation() {
        return calculation;
    }

    public void setCalculation(ChannelSurchargesCalculationDTO calculation) {
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
