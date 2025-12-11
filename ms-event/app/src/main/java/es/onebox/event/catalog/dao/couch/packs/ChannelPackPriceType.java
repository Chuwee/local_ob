package es.onebox.event.catalog.dao.couch.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dao.couch.CatalogPriceSimulation;

import java.io.Serializable;
import java.util.List;

public class ChannelPackPriceType extends IdNameDTO implements Serializable {

    private ChannelPackPrice price;
    private List<CatalogPriceSimulation> simulations;

    public ChannelPackPrice getPrice() {
        return price;
    }

    public void setPrice(ChannelPackPrice price) {
        this.price = price;
    }

    public List<CatalogPriceSimulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<CatalogPriceSimulation> simulations) {
        this.simulations = simulations;
    }
}
