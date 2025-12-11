package es.onebox.event.catalog.dao.couch;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogVenueConfigPricesSimulation implements Serializable {

    @Serial
    private static final long serialVersionUID = -1065007986451059183L;

    private IdNameDTO venueConfig;
    private List<CatalogRate> rates;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<CatalogRate> getRates() {
        return rates;
    }

    public void setRates(List<CatalogRate> rates) {
        this.rates = rates;
    }
}
