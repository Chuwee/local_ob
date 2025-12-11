package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.client.dto.BaseEntityRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.products.enums.ProductEventStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductEventsFilterDTO extends BaseEntityRequestFilter {
    @Serial
    private static final long serialVersionUID = 7959897765345279098L;

    @JsonProperty("event_status")
    private List<EventStatus> eventStatus;
    @JsonProperty("product_event_status")
    private ProductEventStatus status;
    @JsonProperty("start_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
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
