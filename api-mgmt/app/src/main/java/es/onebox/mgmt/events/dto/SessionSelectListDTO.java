package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.CardDesignType;
import es.onebox.mgmt.events.enums.MediaType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionSelectListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8256438622066325485L;

    @JsonProperty("contains_image")
    private Boolean containsImage;
    private MediaType media;
    @JsonProperty("card_design")
    private CardDesignType cardDesignType;
    private Boolean enabled;

    public Boolean getContainsImage() {
        return containsImage;
    }
    public void setContainsImage(Boolean containsImage) {
        this.containsImage = containsImage;
    }

    public MediaType getMedia() { return media; }
    public void setMedia(MediaType media) { this.media = media; }

    public CardDesignType getCardDesignType() { return cardDesignType; }
    public void setCardDesignType(CardDesignType cardDesignType) { this.cardDesignType = cardDesignType; }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
