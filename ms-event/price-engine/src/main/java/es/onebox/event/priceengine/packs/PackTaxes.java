package es.onebox.event.priceengine.packs;

import es.onebox.event.priceengine.taxes.domain.TaxInfo;

import java.util.List;

public class PackTaxes {

    private List<TaxInfo> priceTaxes;

    public List<TaxInfo> getPriceTaxes() {
        return priceTaxes;
    }

    public void setPriceTaxes(List<TaxInfo> priceTaxes) {
        this.priceTaxes = priceTaxes;
    }
}
