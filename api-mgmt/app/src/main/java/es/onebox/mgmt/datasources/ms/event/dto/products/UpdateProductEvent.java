package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ProductEventStatus;
import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SelectionType sessionsSelectionType;
    private ProductEventStatus status;

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
