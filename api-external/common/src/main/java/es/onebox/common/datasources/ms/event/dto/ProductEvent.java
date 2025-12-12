package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.ProductEventStatus;
import es.onebox.common.datasources.ms.event.enums.SelectionType;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductEvent implements Serializable {
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

