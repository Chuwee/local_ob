package es.onebox.event.packs.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RelatedPackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private List<PackItemDTO> items;
    private Boolean suggested;
    private Boolean onSaleForLoggedUsers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PackItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PackItemDTO> items) {
        this.items = items;
    }

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
