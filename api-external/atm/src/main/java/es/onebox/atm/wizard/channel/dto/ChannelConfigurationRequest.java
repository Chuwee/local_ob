package es.onebox.atm.wizard.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

public class ChannelConfigurationRequest implements Serializable {

    private static final long serialVersionUID = -4328524946342206169L;

    @NotEmpty(message = "Promotions must be provided")
    private List<Integer> promotions;

    public List<Integer> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Integer> promotions) {
        this.promotions = promotions;
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
