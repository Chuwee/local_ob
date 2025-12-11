package es.onebox.mgmt.salerequests.pricesimulation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class VenueConfigPricesSimulationDTO implements Serializable {

    private static final long serialVersionUID = 4596785085716303915L;


    @JsonProperty("venue_template")
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
