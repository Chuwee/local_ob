package es.onebox.event.catalog.dao.couch.packs;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.packs.enums.PackPricingType;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class ChannelPackPricesDocument implements Serializable {

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long packId;
    private PackPricingType pricingType;
    private ChannelPackVenueConfigPricesSimulation simulation;
    private List<ChannelPackTaxInfo> taxes;
    private List<ChannelPackTaxInfo> surchargesTaxes;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getPackId() {
        return packId;
    }

    public void setPackId(Long packId) {
        this.packId = packId;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public ChannelPackVenueConfigPricesSimulation getSimulation() {
        return simulation;
    }

    public void setSimulation(ChannelPackVenueConfigPricesSimulation simulation) {
        this.simulation = simulation;
    }

    public List<ChannelPackTaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<ChannelPackTaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<ChannelPackTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ChannelPackTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }
}
