package es.onebox.event.catalog.dao.couch.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.util.List;

public class ChannelPackRate extends IdNameDTO {

    private boolean defaultRate;
    private List<ChannelPackPriceType> priceTypes;

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public List<ChannelPackPriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<ChannelPackPriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
