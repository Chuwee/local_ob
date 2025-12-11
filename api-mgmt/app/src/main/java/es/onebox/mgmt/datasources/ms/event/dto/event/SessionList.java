package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.enums.CardDesignType;
import es.onebox.mgmt.events.enums.MediaType;

import java.io.Serial;
import java.io.Serializable;

public class SessionList implements Serializable {

    @Serial
    private static final long serialVersionUID = -4957835599063540439L;

    private Boolean containsImage;
    private MediaType media;
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

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
