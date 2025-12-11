package es.onebox.event.events.dto;

import java.io.Serializable;

public class UpdateSaleGroupTierDTO implements Serializable {

    private Integer limit;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
