package es.onebox.event.events.dto;

import java.io.Serializable;

public class CreateSaleGroupTierDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
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
