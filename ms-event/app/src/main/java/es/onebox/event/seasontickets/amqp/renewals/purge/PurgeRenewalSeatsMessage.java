package es.onebox.event.seasontickets.amqp.renewals.purge;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.seasontickets.dto.renewals.SeatMappingStatus;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewalStatus;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class PurgeRenewalSeatsMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private SeatMappingStatus mappingStatus;
    private SeatRenewalStatus renewalStatus;
    private String freeSearch;
    private List<String> birthday;

    public PurgeRenewalSeatsMessage() {
    }

    public PurgeRenewalSeatsMessage(Long seasonTicketId, SeatMappingStatus mappingStatus, SeatRenewalStatus renewalStatus, String freeSearch, List<String> birthday) {
        this.seasonTicketId = seasonTicketId;
        this.mappingStatus = mappingStatus;
        this.renewalStatus = renewalStatus;
        this.freeSearch = freeSearch;
        this.birthday = birthday;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public SeatMappingStatus getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(SeatMappingStatus mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public SeatRenewalStatus getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(SeatRenewalStatus renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public List<String> getBirthday() {
        return birthday;
    }

    public void setBirthday(List<String> birthday) {
        this.birthday = birthday;
    }
}
