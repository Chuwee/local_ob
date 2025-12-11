package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.products.enums.ProductEventStatus;
import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductEventDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO product;
    private ProductEventDataDTO event;
    private ProductEventStatus status;
    @JsonProperty("sessions_selection_type")
    private SelectionType sessionsSelectionType;

    public IdNameDTO getProduct() {
        return product;
    }

    public void setProduct(IdNameDTO product) {
        this.product = product;
    }

    public ProductEventDataDTO getEvent() {
        return event;
    }

    public void setEvent(ProductEventDataDTO event) {
        this.event = event;
    }

    public SelectionType getSessionsSelectionType() {
        return sessionsSelectionType;
    }

    public void setSessionsSelectionType(SelectionType sessionsSelectionType) {
        this.sessionsSelectionType = sessionsSelectionType;
    }

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
