package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import java.io.Serializable;

public class UpdatePackChannel implements Serializable {

    private static final long serialVersionUID = -5262397422621445506L;

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
