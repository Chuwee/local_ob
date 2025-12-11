package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductEventStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductEventStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ProductEventStatus status;

    public ProductEventStatus getStatus() {
        return status;
    }

    public void setStatus(ProductEventStatus status) {
        this.status = status;
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

