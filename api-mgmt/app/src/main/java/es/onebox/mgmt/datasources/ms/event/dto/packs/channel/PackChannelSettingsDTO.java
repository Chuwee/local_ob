package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class PackChannelSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean suggested;
    @JsonProperty("on_sale_for_logged_users")
    private Boolean onSaleForLoggedUsers;

    public Boolean getSuggested() {
        return suggested;
    }

    public void setSuggested(Boolean suggested) {
        this.suggested = suggested;
    }

    public Boolean getOnSaleForLoggedUsers() {
        return onSaleForLoggedUsers;
    }

    public void setOnSaleForLoggedUsers(Boolean onSaleForLoggedUsers) {
        this.onSaleForLoggedUsers = onSaleForLoggedUsers;
    }
}
