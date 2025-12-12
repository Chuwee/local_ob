package es.onebox.common.datasources.accesscontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class TicketFilter extends BaseRequestFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 25)
    @JsonProperty("session_id")
    private List<Long> sessionId;
    @JsonProperty("event_id")
    private List<Long> eventId;
    @JsonProperty("venue_id")
    private List<Long> venueId;
    private String barcode;
    @JsonProperty("updated_from")
    private ZonedDateTime updatedFrom;
    private List<TicketValidationStatus> status;
    private SortOperator<TicketSortableField> sort;
    private BarcodeOrderProvider provider;

    public List<Long> getSessionId() {
        return sessionId;
    }

    public void setSessionId(List<Long> sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ZonedDateTime getUpdatedFrom() {
        return updatedFrom;
    }

    public void setUpdatedFrom(ZonedDateTime updatedFrom) {
        this.updatedFrom = updatedFrom;
    }

    public List<TicketValidationStatus> getStatus() {
        return status;
    }

    public void setStatus(List<TicketValidationStatus> status) {
        this.status = status;
    }

    public SortOperator<TicketSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<TicketSortableField> sort) {
        this.sort = sort;
    }

    public BarcodeOrderProvider getProvider() {
        return provider;
    }

    public void setProvider(BarcodeOrderProvider provider) {
        this.provider = provider;
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
