package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.dto.ProductEventData;
import es.onebox.common.datasources.ms.event.enums.ProductEventStatus;
import es.onebox.common.datasources.ms.event.enums.SelectionType;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductEventFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7890123456789012345L;

    private IdNameDTO product;
    private ProductEventData event;
    private ProductEventStatus status;
    private SelectionType sessionsSelectionType;

    public ProductEventFeverDTO() {
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

    public ProductEventStatus getStatus() {
        return status;
    }

    public void setStatus(ProductEventStatus status) {
        this.status = status;
    }

    public SelectionType getSessionsSelectionType() {
        return sessionsSelectionType;
    }

    public void setSessionsSelectionType(SelectionType sessionsSelectionType) {
        this.sessionsSelectionType = sessionsSelectionType;
    }
}
