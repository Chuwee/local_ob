package es.onebox.event.catalog.dto.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;
import java.util.List;

public class CatalogPackRateDTO extends IdNameDTO implements Serializable {

    private boolean defaultRate;
    private List<CatalogPackPriceTypeDTO> priceTypes;

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public List<CatalogPackPriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<CatalogPackPriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
