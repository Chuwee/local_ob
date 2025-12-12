package es.onebox.common.datasources.ms.event.dto;

import java.io.Serializable;
import java.util.List;

public class EventChannelDTO implements Serializable {
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
