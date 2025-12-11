package es.onebox.mgmt.collectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.collectives.dto.Scope;
import es.onebox.mgmt.collectives.dto.Status;
import es.onebox.mgmt.collectives.dto.Type;
import es.onebox.mgmt.collectives.dto.ValidationMethod;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.Set;

@MaxLimit(50)
@DefaultLimit(50)
public class CollectivesRequest extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private Set<Long> id;
    @JsonProperty("entity_id")
    @Size(max = 50)
    private Set<Long> entityId;
    @JsonProperty("entity_admin_id")
    private Long entityAdminId;
    private Set<Type> type;
    private Set<Scope> scope;
    private Set<Status> status;
    @JsonProperty("validation_method")
    private Set<ValidationMethod> validationMethod;
    private SortOperator<String> sort;
    private String q;

    public Set<Long> getId() {
        return id;
    }

    public void setId(Set<Long> id) {
        this.id = id;
    }

    public Set<Long> getEntityId() {
        return entityId;
    }

    public Set<Type> getType() {
        return type;
    }

    public void setType(Set<Type> type) {
        this.type = type;
    }

    public void setEntityId(Set<Long> entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Set<Scope> getScope() {
        return scope;
    }

    public void setScope(Set<Scope> scope) {
        this.scope = scope;
    }

    public Set<Status> getStatus() {
        return status;
    }

    public void setStatus(Set<Status> status) {
        this.status = status;
    }

    public Set<ValidationMethod> getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(Set<ValidationMethod> validationMethod) {
        this.validationMethod = validationMethod;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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
