package es.onebox.event.catalog.dao.couch.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;
import java.util.List;

public class ChannelPackVenueConfigPricesSimulation implements Serializable {

    private IdNameDTO venueConfig;
    private List<ChannelPackRate> rates;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<ChannelPackRate> getRates() {
        return rates;
    }

    public void setRates(List<ChannelPackRate> rates) {
        this.rates = rates;
    }
}
