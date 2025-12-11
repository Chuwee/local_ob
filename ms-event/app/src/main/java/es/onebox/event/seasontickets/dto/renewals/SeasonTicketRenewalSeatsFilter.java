package es.onebox.event.seasontickets.dto.renewals;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.event.seasontickets.elasticsearch.RenewalsSortableField;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class SeasonTicketRenewalSeatsFilter extends BaseRequestFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private Long entityId;
    private SeatMappingStatus mappingStatus;
    private SeatRenewalStatus renewalStatus;
    private List<String> userIds;
    private List<String> renewalIds;
    private Long actualRateId;
    private String freeSearch;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> birthday;
    private SortOperator<RenewalsSortableField> sort;
    private String renewalSubstatus;
    private Boolean strictStatus;
    private Boolean autoRenewal;

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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getRenewalIds() {
        return renewalIds;
    }

    public void setRenewalIds(List<String> renewalIds) {
        this.renewalIds = renewalIds;
    }

    public Long getActualRateId() {
        return actualRateId;
    }

    public void setActualRateId(Long actualRateId) {
        this.actualRateId = actualRateId;
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

    public SortOperator<RenewalsSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<RenewalsSortableField> sort) {
        this.sort = sort;
    }

    public Long getEntityId() {return entityId;}

    public void setEntityId(Long entityId) {this.entityId = entityId;}

    public String getRenewalSubstatus() { return renewalSubstatus; }

    public void setRenewalSubstatus(String renewalSubstatus) { this.renewalSubstatus = renewalSubstatus; }

    public Boolean getStrictStatus() {
        return strictStatus;
    }

    public void setStrictStatus(Boolean strictStatus) {
        this.strictStatus = strictStatus;
    }

    public Boolean getAutoRenewal() { return autoRenewal; }

    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }
}
