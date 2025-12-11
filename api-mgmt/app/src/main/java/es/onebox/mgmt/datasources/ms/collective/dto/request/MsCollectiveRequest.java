package es.onebox.mgmt.datasources.ms.collective.dto.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveStatus;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveType;
import es.onebox.mgmt.datasources.ms.collective.dto.CollectiveValidationMethod;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class MsCollectiveRequest extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 7781909371453107314L;

    private Set<Long> id;
    private Set<Long> operatorId;
    private Set<Long> entityId;
    private Long entityAdminId;
    private Set<CollectiveStatus> status;
    private Set<CollectiveType> type;
    private Set<CollectiveValidationMethod> validationMethod;
    private SortOperator<String> sort;
    private String q;

    public Set<Long> getId() {
        return id;
    }

    public void setId(Set<Long> id) {
        this.id = id;
    }

    public Set<Long> getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Set<Long> operatorId) {
        this.operatorId = operatorId;
    }

    public Set<Long> getEntityId() {
        return entityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public void setEntityId(Set<Long> entityId) {
        this.entityId = entityId;
    }

    public Set<CollectiveStatus> getStatus() {
        return status;
    }

    public void setStatus(Set<CollectiveStatus> status) {
        this.status = status;
    }

    public Set<CollectiveType> getType() {
        return type;
    }

    public void setType(Set<CollectiveType> type) {
        this.type = type;
    }

    public Set<CollectiveValidationMethod> getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(Set<CollectiveValidationMethod> validationMethod) {
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
