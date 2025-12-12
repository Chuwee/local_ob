package es.onebox.common.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.entities.enums.EntityStatus;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(1000)
public class EntitySearchFilterDTO extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("operator_id")
    private Long operatorId;

    private EntityTypes type;

    private EntityStatus status;

    @JsonProperty("allow_avet_integration")
    private Boolean allowAvetIntegration;

    @JsonProperty("allow_members")
    private Boolean allowMembers;

    @JsonProperty("q")
    private String freeSearch;

    private SortOperator<String> sort;

    private List<String> fields;

    @JsonProperty("b2b_enabled")
    private Boolean b2bEnabled;

    @JsonProperty("allow_digital_season_ticket")
    private Boolean allowDigitalSeasonTicket;

    @JsonProperty("allow_massive_email")
    private Boolean allowMassiveEmail;

    @JsonProperty("include_entity_admin")
    private Boolean includeEntityAdmin;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public EntityTypes getType() {
        return type;
    }

    public void setType(EntityTypes type) {
        this.type = type;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
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

    public Boolean getIncludeEntityAdmin() {
        return includeEntityAdmin;
    }

    public void setIncludeEntityAdmin(Boolean includeEntityAdmin) {
        this.includeEntityAdmin = includeEntityAdmin;
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
