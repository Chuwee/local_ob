package es.onebox.event.pricesengine.dto;

import java.io.Serializable;
import java.util.List;

public class VenueConfigPricesSimulationDTO implements Serializable {
    private static final long serialVersionUID = -8911305536418522115L;

    private VenueConfigBaseDTO venueConfig;
    private List<RateDTO> rates;

    public VenueConfigBaseDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(VenueConfigBaseDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }
}
