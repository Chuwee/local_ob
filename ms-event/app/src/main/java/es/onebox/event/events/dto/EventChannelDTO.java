package es.onebox.event.events.dto;

import java.util.List;

public class EventChannelDTO extends BaseEventChannelDTO {

    private static final long serialVersionUID = 1L;
    
    private Boolean useAllSaleGroups;
    private List<SaleGroupDTO> saleGroups;

    public Boolean getUseAllSaleGroups() {
        return useAllSaleGroups;
    }

    public void setUseAllSaleGroups(Boolean useAllSaleGroups) {
        this.useAllSaleGroups = useAllSaleGroups;
    }

    public List<SaleGroupDTO> getSaleGroups() {
        return saleGroups;
    }

    public void setSaleGroups(List<SaleGroupDTO> saleGroups) {
        this.saleGroups = saleGroups;
    }

}
