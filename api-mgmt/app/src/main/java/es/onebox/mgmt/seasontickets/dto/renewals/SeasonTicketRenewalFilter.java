package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.seasontickets.enums.SeatMappingStatus;
import es.onebox.mgmt.seasontickets.enums.SeatRenewalStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class SeasonTicketRenewalFilter extends BaseRequestFilter {
    private static final long serialVersionUID = 1L;

    @JsonProperty("mapping_status")
    private SeatMappingStatus mappingStatus;
    @JsonProperty("renewal_status")
    private SeatRenewalStatus renewalStatus;
    @JsonProperty("renewal_substatus")
    private String renewalSubstatus;
    @JsonProperty("auto_renewal")
    private Boolean autoRenewal;
    @JsonProperty("q")
    private String freeSearch;
    @JsonProperty("entity_id")
    private Long entityId;
    private SortOperator<String> sort;
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

    public String getRenewalSubstatus() {
        return renewalSubstatus;
    }

    public void setRenewalSubstatus(String renewalSubstatus) {
        this.renewalSubstatus = renewalSubstatus;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<FilterWithOperator<ZonedDateTime>> getBirthday() {
        return birthday;
    }

    public void setBirthday(List<FilterWithOperator<ZonedDateTime>> birthday) {
        this.birthday = birthday;
    }
}