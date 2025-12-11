package es.onebox.event.catalog.dto.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dto.CatalogTaxInfoDTO;
import es.onebox.event.packs.enums.PackPricingType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPackPricesSimulationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1065007986451059183L;

    private IdNameDTO venueConfig;
    private PackPricingType pricingType;
    private List<CatalogPackRateDTO> rates;
    private List<CatalogTaxInfoDTO> taxes;
    private List<CatalogTaxInfoDTO> surchargesTaxes;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public PackPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PackPricingType pricingType) {
        this.pricingType = pricingType;
    }

    public List<CatalogPackRateDTO> getRates() {
        return rates;
    }

    public void setRates(List<CatalogPackRateDTO> rates) {
        this.rates = rates;
    }

    public List<CatalogTaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<CatalogTaxInfoDTO> taxes) {
        this.taxes = taxes;
    }

    public List<CatalogTaxInfoDTO> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<CatalogTaxInfoDTO> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }
}
