package es.onebox.event.packs.dto;

import java.io.Serializable;

public class PackChannelSettingsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean suggested;
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
