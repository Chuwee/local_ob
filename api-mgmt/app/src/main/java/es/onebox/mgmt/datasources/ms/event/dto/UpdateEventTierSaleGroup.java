package es.onebox.mgmt.datasources.ms.event.dto;

import java.io.Serializable;

public class UpdateEventTierSaleGroup implements Serializable {

    private Integer limit;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
