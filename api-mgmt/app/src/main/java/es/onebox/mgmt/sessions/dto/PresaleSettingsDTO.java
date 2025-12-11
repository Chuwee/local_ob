package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class PresaleSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4004334252578373357L;

    @JsonProperty("multiple_purchase")
    private Boolean multiplePurchase;


    public Boolean getMultiplePurchase() {
        return multiplePurchase;
    }

    public void setMultiplePurchase(Boolean multiplePurchase) {
        this.multiplePurchase = multiplePurchase;
    }
}
