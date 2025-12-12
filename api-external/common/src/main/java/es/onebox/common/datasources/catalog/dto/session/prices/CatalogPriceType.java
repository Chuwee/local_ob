package es.onebox.common.datasources.catalog.dto.session.prices;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceType extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6115511681553022650L;
    private CatalogPrice price;
    private List<CatalogPriceSimulation> simulations;

    public List<CatalogPriceSimulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<CatalogPriceSimulation> simulations) {
        this.simulations = simulations;
    }

    public CatalogPrice getPrice() {
        return price;
    }

    public void setPrice(CatalogPrice catalogPriceDTO) {
        this.price = catalogPriceDTO;
    }
}
