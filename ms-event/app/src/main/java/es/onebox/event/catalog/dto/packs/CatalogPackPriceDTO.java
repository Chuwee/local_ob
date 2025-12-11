package es.onebox.event.catalog.dto.packs;

import es.onebox.event.catalog.dto.price.CatalogPriceTaxesDTO;
import es.onebox.event.catalog.dto.price.CatalogSurchargeDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPackPriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8880904812495827148L;

    private Double total;
    private Double net;
    private List<ChannelPackPriceItemInfoDTO> itemsInfo;
    private CatalogPriceTaxesDTO taxes;
    private List<CatalogSurchargeDTO> surcharges;

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

    public List<ChannelPackPriceItemInfoDTO> getItemsInfo() {
        return itemsInfo;
    }

    public void setItemsInfo(List<ChannelPackPriceItemInfoDTO> itemsInfo) {
        this.itemsInfo = itemsInfo;
    }

    public CatalogPriceTaxesDTO getTaxes() {
        return taxes;
    }

    public void setTaxes(CatalogPriceTaxesDTO taxes) {
        this.taxes = taxes;
    }

    public List<CatalogSurchargeDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<CatalogSurchargeDTO> surcharges) {
        this.surcharges = surcharges;
    }
}
