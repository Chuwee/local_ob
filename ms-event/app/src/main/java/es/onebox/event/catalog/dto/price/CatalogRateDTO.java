package es.onebox.event.catalog.dto.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.util.List;

public class CatalogRateDTO extends IdNameDTO  {

    @Serial
    private static final long serialVersionUID = -3245860872916345653L;
    @JsonProperty("default_rate")
    private boolean defaultRate;
    @JsonProperty("price_types")
    private List<CatalogPriceTypeDTO> priceTypes;

    public CatalogRateDTO() {
    }

    public CatalogRateDTO(Long id, String name, List<CatalogPriceTypeDTO> priceTypes) {
        super(id, name);
        this.priceTypes = priceTypes;
    }

    public List<CatalogPriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<CatalogPriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
    }

    public boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }
}
