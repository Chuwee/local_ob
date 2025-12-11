package es.onebox.event.catalog.dto.price;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceTypeDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3725515646908112192L;

    private CatalogPriceDTO price;
    private List<CatalogPriceSimulationDTO> simulations;

    public List<CatalogPriceSimulationDTO> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<CatalogPriceSimulationDTO> simulations) {
        this.simulations = simulations;
    }

    public CatalogPriceDTO getPrice() {
        return price;
    }

    public void setPrice(CatalogPriceDTO catalogPriceDTO) {
        this.price = catalogPriceDTO;
    }
}
