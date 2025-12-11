package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.util.List;

public class EventChannel extends BaseEventChannel {

    private static final long serialVersionUID = 1L;

    private Boolean useAllSaleGroups;
    private List<SaleGroup> saleGroups;

    public Boolean getUseAllSaleGroups() {
        return useAllSaleGroups;
    }

    public void setUseAllSaleGroups(Boolean useAllSaleGroups) {
        this.useAllSaleGroups = useAllSaleGroups;
    }

    public List<SaleGroup> getSaleGroups() {
        return saleGroups;
    }

    public void setSaleGroups(List<SaleGroup> saleGroups) {
        this.saleGroups = saleGroups;
    }
}
