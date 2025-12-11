package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;

import java.io.Serializable;
import java.util.List;

public class PackPriceType extends IdNameDTO implements Serializable {

    private PackPrice price;
    private List<PriceSimulation> simulations;

    public PackPrice getPrice() {
        return price;
    }

    public void setPrice(PackPrice price) {
        this.price = price;
    }

    public List<PriceSimulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<PriceSimulation> simulations) {
        this.simulations = simulations;
    }
}
