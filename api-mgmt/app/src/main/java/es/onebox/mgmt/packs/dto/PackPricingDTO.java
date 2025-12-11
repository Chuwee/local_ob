package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.PackPricingType;

public class PackPricingDTO {

    private PackPricingType type;

    @JsonProperty("price_increment")
    private Double priceIncrement;

    public PackPricingType getType() {
        return type;
    }

    public void setType(PackPricingType type) {
        this.type = type;
    }

    public Double getPriceIncrement() {
        return priceIncrement;
    }

    public void setPriceIncrement(Double priceIncrement) {
        this.priceIncrement = priceIncrement;
    }
}
