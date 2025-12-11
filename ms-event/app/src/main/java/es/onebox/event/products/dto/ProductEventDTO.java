package es.onebox.event.products.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductEventDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private ProductEventData event;
    private ProductEventStatus status;
    private SelectionType sessionsSelectionType;

    public ProductEventStatus getStatus() {
        return status;
    }

    public void setStatus(ProductEventStatus status) {
        this.status = status;
    }

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public ProductEventData getEvent() {
        return event;
    }

    public void setEvent(ProductEventData event) {
        this.event = event;
    }

    public SelectionType getSessionsSelectionType() {
        return sessionsSelectionType;
    }

    public void setSessionsSelectionType(SelectionType sessionsSelectionType) {
        this.sessionsSelectionType = sessionsSelectionType;
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
