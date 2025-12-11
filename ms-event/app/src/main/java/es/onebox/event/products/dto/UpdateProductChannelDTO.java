package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean checkoutSuggestionEnabled;
    private Boolean standaloneEnabled;

    public Boolean getCheckoutSuggestionEnabled() {
        return checkoutSuggestionEnabled;
    }

    public void setCheckoutSuggestionEnabled(Boolean checkoutSuggestionEnabled) {
        this.checkoutSuggestionEnabled = checkoutSuggestionEnabled;
    }

    public Boolean getStandaloneEnabled() {
        return standaloneEnabled;
    }

    public void setStandaloneEnabled(Boolean standaloneEnabled) {
        this.standaloneEnabled = standaloneEnabled;
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

