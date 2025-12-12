package es.onebox.common.datasources.ms.order.dto;

import java.io.Serializable;

public class InformativePrices implements Serializable {
    private Double insurance;
    private Double packItem;

    public InformativePrices(Double insurance) {
        this.insurance = insurance;
    }

    public InformativePrices() {
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public Double getPackItem() {
        return packItem;
    }

    public void setPackItem(Double packItem) {
        this.packItem = packItem;
    }
}
