package es.onebox.event.priceengine.simulation.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueConfigPricesSimulation implements Serializable {
    @Serial
    private static final long serialVersionUID = -8911305536418522115L;

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
