package es.onebox.event.catalog.dto.price;

import es.onebox.event.pricesengine.dto.enums.SurchargeType;

import java.io.Serial;
import java.io.Serializable;

public class CatalogSurchargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4529214809368323287L;

    private Double value;
    private Double net;
    private CatalogPriceTaxesDTO taxes;
    private SurchargeType type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public CatalogPriceTaxesDTO getTaxes() {
        return taxes;
    }

    public void setTaxes(CatalogPriceTaxesDTO taxes) {
        this.taxes = taxes;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }
}
