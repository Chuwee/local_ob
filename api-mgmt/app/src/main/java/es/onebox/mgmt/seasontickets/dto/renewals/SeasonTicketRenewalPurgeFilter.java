package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.mgmt.seasontickets.enums.SeatMappingStatus;
import es.onebox.mgmt.seasontickets.enums.SeatRenewalStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeasonTicketRenewalPurgeFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("mapping_status")
    private SeatMappingStatus mappingStatus;
    @JsonProperty("renewal_status")
    private SeatRenewalStatus renewalStatus;
    @JsonProperty("q")
    private String freeSearch;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> birthday;

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

    public List<FilterWithOperator<ZonedDateTime>> getBirthday() {
        return birthday;
    }

    public void setBirthday(List<FilterWithOperator<ZonedDateTime>> birthday) {
        this.birthday = birthday;
    }
}
