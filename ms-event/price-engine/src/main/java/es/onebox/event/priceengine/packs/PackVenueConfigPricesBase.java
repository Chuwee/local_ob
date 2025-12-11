package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import java.util.List;

public class PackVenueConfigPricesBase {

    private IdNameDTO venueConfig;
    private List<PackRateBase> rates;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<PackRateBase> getRates() {
        return rates;
    }

    public void setRates(List<PackRateBase> rates) {
        this.rates = rates;
    }
}
