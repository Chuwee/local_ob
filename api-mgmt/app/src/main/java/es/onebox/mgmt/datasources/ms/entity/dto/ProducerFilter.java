package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.entities.enums.ProducerStatus;

import java.util.List;

public class ProducerFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Long entityId;
    private Long entityAdminId;
    private Long operatorId;
    private FilterWithOperator<ProducerStatus> status;
    private Boolean defaultPromoter;
    private Boolean includeDeleted;
    private String freeSearch;
    private SortOperator<String> sort;
    private List<String> fields;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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

    public FilterWithOperator<ProducerStatus> getStatus() {
        return status;
    }

    public void setStatus(FilterWithOperator<ProducerStatus> status) {
        this.status = status;
    }

    public Boolean getDefaultPromoter() {
        return defaultPromoter;
    }

    public void setDefaultPromoter(Boolean defaultPromoter) {
        this.defaultPromoter = defaultPromoter;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
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
}
