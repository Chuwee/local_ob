package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PackItemProductDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    @JsonProperty("variant")
    private IdNameDTO variant;

    @JsonProperty("delivery_point")
    private IdNameDTO deliveryPoint;

    @JsonProperty("shared_barcode")
    private Boolean sharedBarcode;

    public IdNameDTO getVariant() {
        return variant;
    }

    public void setVariant(IdNameDTO variant) {
        this.variant = variant;
    }

    public IdNameDTO getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(IdNameDTO deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public Boolean getSharedBarcode() {
        return sharedBarcode;
    }

    public void setSharedBarcode(Boolean sharedBarcode) {
        this.sharedBarcode = sharedBarcode;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
