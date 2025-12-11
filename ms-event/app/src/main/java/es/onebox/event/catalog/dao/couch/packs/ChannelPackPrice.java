package es.onebox.event.catalog.dao.couch.packs;

import es.onebox.event.catalog.dao.couch.CatalogPriceTaxes;
import es.onebox.event.catalog.dao.couch.CatalogSurcharge;

import java.io.Serializable;
import java.util.List;

public class ChannelPackPrice implements Serializable {

    private Double total;
    private Double net;
    private List<ChannelPackPriceItemInfo> itemsInfo;
    private CatalogPriceTaxes taxes;
    private List<CatalogSurcharge> surcharges;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public List<ChannelPackPriceItemInfo> getItemsInfo() {
        return itemsInfo;
    }

    public void setItemsInfo(List<ChannelPackPriceItemInfo> itemsInfo) {
        this.itemsInfo = itemsInfo;
    }

    public CatalogPriceTaxes getTaxes() {
        return taxes;
    }

    public void setTaxes(CatalogPriceTaxes taxes) {
        this.taxes = taxes;
    }

    public List<CatalogSurcharge> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<CatalogSurcharge> surcharges) {
        this.surcharges = surcharges;
    }
}
