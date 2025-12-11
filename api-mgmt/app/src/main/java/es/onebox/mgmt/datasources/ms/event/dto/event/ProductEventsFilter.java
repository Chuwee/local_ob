package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.products.enums.ProductEventStatus;

import java.io.Serial;

import java.time.ZonedDateTime;
import java.util.List;

public class ProductEventsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 7959897765345279098L;

    private List<EventStatus> eventStatus;
    private ProductEventStatus status;
    private ZonedDateTime startDate;

    public List<EventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }

    public ProductEventStatus getStatus() {
        return status;
    }

    public void setStatus(ProductEventStatus status) {
        this.status = status;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }
}
