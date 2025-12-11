package es.onebox.mgmt.entities.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;

public class SubscriptionRequestFilter extends BaseRequestFilter {
    private Boolean filterActive;
    private String filterName;
    private Long entityAdminId;
    private Long operatorId;

    public Boolean getFilterActive() {
        return filterActive;
    }

    public void setFilterActive(Boolean filterActive) {
        this.filterActive = filterActive;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
