package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.util.List;

public class PackRate extends IdNameDTO {

    private boolean defaultRate;
    private List<PackPriceType> priceTypes;

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public List<PackPriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PackPriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
