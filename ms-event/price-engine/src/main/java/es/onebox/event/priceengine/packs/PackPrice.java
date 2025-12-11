package es.onebox.event.priceengine.packs;

import java.util.List;

public class PackPrice {

    private Double total;
    private List<PackPriceItemInfo> itemsInfo;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<PackPriceItemInfo> getItemsInfo() {
        return itemsInfo;
    }

    public void setItemsInfo(List<PackPriceItemInfo> itemsInfo) {
        this.itemsInfo = itemsInfo;
    }
}
