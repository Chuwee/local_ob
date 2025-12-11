package es.onebox.mgmt.salerequests.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class SubscriptionListSalesRequestDTO implements Serializable {

    private static final long serialVersionUID = -572949894294740981L;

    @NotNull
    private Boolean enable;
    private Integer id;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
