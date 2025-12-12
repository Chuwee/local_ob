package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.entities.enums.EntityStatus;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(1000)
public class EntitySearchFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer operatorId;
    private Integer entityAdminId;
    private String name;
    private String shortName;
    private FilterWithOperator<EntityStatus> status;
    private EntityTypes type;
    private String freeSearch;
    private Boolean allowAvetIntegration;
    private Boolean allowMembers;
    private SortOperator<String> sort;
    private List<String> fields;
    private List<Integer> ids;
    private Boolean b2bEnabled;
    private Boolean allowDigitalSeasonTicket;
    private Boolean allowMassiveEmail;
    private Boolean includeEntityAdmin;
    private Boolean includeDeleted;
    private Boolean allowFeverZone;
    private Boolean operatorAllowFeverZone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Integer entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public FilterWithOperator<EntityStatus> getStatus() {
        return status;
    }

    public void setStatus(FilterWithOperator<EntityStatus> status) {
        this.status = status;
    }

    public EntityTypes getType() {
        return type;
    }

    public void setType(EntityTypes type) {
        this.type = type;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
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

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
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

    public Boolean getIncludeEntityAdmin() {
        return includeEntityAdmin;
    }

    public void setIncludeEntityAdmin(Boolean includeEntityAdmin) {
        this.includeEntityAdmin = includeEntityAdmin;
    }

    public Boolean getAllowMassiveEmail() {
        return allowMassiveEmail;
    }

    public void setAllowMassiveEmail(Boolean allowMassiveEmail) {
        this.allowMassiveEmail = allowMassiveEmail;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public Boolean getAllowFeverZone() {
        return allowFeverZone;
    }

    public void setAllowFeverZone(Boolean allowFeverZone) {
        this.allowFeverZone = allowFeverZone;
    }

    public Boolean getOperatorAllowFeverZone() {
        return operatorAllowFeverZone;
    }

    public void setOperatorAllowFeverZone(Boolean operatorAllowFeverZone) {
        this.operatorAllowFeverZone = operatorAllowFeverZone;
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
