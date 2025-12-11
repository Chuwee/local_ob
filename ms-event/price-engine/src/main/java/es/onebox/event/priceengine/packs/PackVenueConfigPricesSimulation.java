package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;

import java.io.Serializable;
import java.util.List;

public class PackVenueConfigPricesSimulation implements Serializable {

    private IdNameDTO venueConfig;
    private List<PackRate> rates;
    private List<TaxInfo> taxes;
    private List<TaxInfo> surchargesTaxes;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<PackRate> getRates() {
        return rates;
    }

    public void setRates(List<PackRate> rates) {
        this.rates = rates;
    }

    public List<TaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<TaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<TaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }
}
