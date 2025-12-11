package es.onebox.event.seasontickets.dto.renewals;

import es.onebox.core.serializer.dto.request.FilterWithOperator;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class RenewalSeatsPurgeFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SeatMappingStatus mappingStatus;
    private SeatRenewalStatus renewalStatus;
    private String freeSearch;
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
