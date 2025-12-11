package es.onebox.mgmt.events.dto;

import java.io.Serializable;

public class UpdateTierQuotaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer limit;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
