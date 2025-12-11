package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class EntitySearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -6530774819153596252L;

    private Long operatorId;
    private String type;
    private FilterWithOperator<String> status;
    private Boolean allowAvetIntegration;
    private Boolean allowMembers;
    private String freeSearch;
    private SortOperator<String> sort;
    private List<String> fields;
    private Boolean b2bEnabled;
    private Boolean allowDigitalSeasonTicket;
    private Boolean allowMassiveEmail;
    private Long entityAdminId;
    private Boolean includeEntityAdmin;
    private List<Integer> ids;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FilterWithOperator<String> getStatus() {
        return status;
    }

    public void setStatus(FilterWithOperator<String> status) {
        this.status = status;
    }

    public Boolean getAllowAvetIntegration() {
        return allowAvetIntegration;
    }

    public void setAllowAvetIntegration(Boolean allowAvetIntegration) {
        this.allowAvetIntegration = allowAvetIntegration;
    }

    public Boolean getAllowMembers() {
        return allowMembers;
    }

    public void setAllowMembers(Boolean allowMembers) {
        this.allowMembers = allowMembers;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Boolean getB2bEnabled() {
        return b2bEnabled;
    }

    public void setB2bEnabled(Boolean b2bEnabled) {
        this.b2bEnabled = b2bEnabled;
    }

    public Boolean getAllowDigitalSeasonTicket() {
        return allowDigitalSeasonTicket;
    }

    public void setAllowDigitalSeasonTicket(Boolean allowDigitalSeasonTicket) {
        this.allowDigitalSeasonTicket = allowDigitalSeasonTicket;
    }

    public Boolean getAllowMassiveEmail() {
        return allowMassiveEmail;
    }

    public void setAllowMassiveEmail(Boolean allowMassiveEmail) {
        this.allowMassiveEmail = allowMassiveEmail;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Boolean getIncludeEntityAdmin() {
        return includeEntityAdmin;
    }

    public void setIncludeEntityAdmin(Boolean includeEntityAdmin) {
        this.includeEntityAdmin = includeEntityAdmin;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
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
