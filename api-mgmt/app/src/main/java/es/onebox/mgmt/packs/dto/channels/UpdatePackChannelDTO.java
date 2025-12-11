package es.onebox.mgmt.packs.dto.channels;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UpdatePackChannelDTO implements Serializable {

    private static final long serialVersionUID = -5262397422621445506L;

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
