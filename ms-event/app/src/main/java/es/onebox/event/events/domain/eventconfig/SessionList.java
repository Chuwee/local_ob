package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.events.enums.CardDesignType;
import es.onebox.event.events.enums.MediaType;

import java.io.Serial;
import java.io.Serializable;

public class SessionList implements Serializable {

    @Serial
    private static final long serialVersionUID = -4957835599063540439L;

    private Boolean listContainsImage;
    private MediaType media;
    private CardDesignType cardDesignType;
    private Boolean enabled;

    public SessionList() {
    }

    public SessionList(Boolean listContainsImage) {
        this.listContainsImage = listContainsImage;
    }

    public Boolean getListContainsImage() {
        return listContainsImage;
    }

    public void setListContainsImage(Boolean listContainsImage) {
        this.listContainsImage = listContainsImage;
    }

    public MediaType getMedia() { return media; }

    public void setMedia(MediaType media) { this.media = media; }

    public CardDesignType getCardDesignType() { return cardDesignType; }

    public void setCardDesignType(CardDesignType cardDesignType) {
        this.cardDesignType = cardDesignType;
    }

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
