package es.onebox.event.catalog.dto.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceSimulationDTO;

import java.io.Serializable;
import java.util.List;

public class CatalogPackPriceTypeDTO extends IdNameDTO implements Serializable {

    private CatalogPackPriceDTO price;
    private List<CatalogPriceSimulationDTO> simulations;

    public CatalogPackPriceDTO getPrice() {
        return price;
    }

    public void setPrice(CatalogPackPriceDTO price) {
        this.price = price;
    }

    public List<CatalogPriceSimulationDTO> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<CatalogPriceSimulationDTO> simulations) {
        this.simulations = simulations;
    }
}
