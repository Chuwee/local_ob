package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueConfigPricesSimulation implements Serializable {

    @Serial
    private static final long serialVersionUID = 4596785085716303915L;


    private VenueConfigBase venueConfig;
    private List<Rate> rates;

    public VenueConfigBase getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(VenueConfigBase venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }
}
