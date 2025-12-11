package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.util.List;

public class PackRateBase extends IdNameDTO {

    private boolean defaultRate;
    private List<PackPriceTypeBase> priceTypes;

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public List<PackPriceTypeBase> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<PackPriceTypeBase> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
