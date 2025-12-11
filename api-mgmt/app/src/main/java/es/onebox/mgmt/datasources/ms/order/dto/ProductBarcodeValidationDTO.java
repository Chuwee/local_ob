package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.mgmt.datasources.ms.order.enums.ProductBarcodeValidationStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProductBarcodeValidationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProductBarcodeValidationStatus status;
    private ZonedDateTime date;

    public ProductBarcodeValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ProductBarcodeValidationStatus status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
