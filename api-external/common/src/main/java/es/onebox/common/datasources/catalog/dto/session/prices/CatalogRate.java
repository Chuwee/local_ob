package es.onebox.common.datasources.catalog.dto.session.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CatalogRate extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6305844149784341129L;
    @JsonProperty("default")
    private boolean defaultRate;
    @JsonProperty("price_types")
    private List<CatalogPriceType> priceTypes;
    @JsonProperty("texts")
    private Map<String, Map<String, String>> texts;

    public CatalogRate() {
    }

    public CatalogRate(Long id, String name, List<CatalogPriceType> priceTypes) {
        super(id, name);
        this.priceTypes = priceTypes;
    }

    public List<CatalogPriceType> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<CatalogPriceType> priceTypes) {
        this.priceTypes = priceTypes;
    }

    @JsonProperty("default")
    public boolean isDefaultRate() {
        return defaultRate;
    }

    @JsonProperty("default_rate")
    public void setDefaultRate(boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Map<String, Map<String, String>> getTexts() { return texts; }

    public void setTexts(Map<String, Map<String, String>> texts) { this.texts = texts; }
}
