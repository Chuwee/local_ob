package es.onebox.mgmt.datasources.ms.channel.dto;


import es.onebox.mgmt.datasources.ms.channel.enums.PriceDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.enums.TaxesDisplayMode;

import java.io.Serial;
import java.io.Serializable;

public class PriceDisplay implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private TaxesDisplayMode taxesDisplayMode;
    private PriceDisplayMode priceDisplayMode;


    public PriceDisplay() {
    }

    public PriceDisplay(PriceDisplayMode priceDisplayMode, TaxesDisplayMode taxesDisplayMode) {
        this.priceDisplayMode = priceDisplayMode;
        this.taxesDisplayMode = taxesDisplayMode;
    }

    public TaxesDisplayMode getTaxesDisplayMode() {
        return taxesDisplayMode;
    }

    public void setTaxesDisplayMode(TaxesDisplayMode taxesDisplayMode) {
        this.taxesDisplayMode = taxesDisplayMode;
    }

    public PriceDisplayMode getPriceDisplayMode() {
        return priceDisplayMode;
    }

    public void setPriceDisplayMode(PriceDisplayMode priceDisplayMode) {
        this.priceDisplayMode = priceDisplayMode;
    }
}
