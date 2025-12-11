package es.onebox.mgmt.datasources.ms.event.dto;

import java.io.Serializable;

public class CreateEventTierSaleGroup implements Serializable {

    private Long saleGroupId;
    private Integer limit;


    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Long getSaleGroupId() {
        return saleGroupId;
    }

    public void setSaleGroupId(Long saleGroupId) {
        this.saleGroupId = saleGroupId;
    }
}
