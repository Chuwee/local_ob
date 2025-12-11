package es.onebox.mgmt.datasources.ms.ticket.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.ms.ticket.enums.PassbookTemplateType;
import es.onebox.mgmt.passbook.dto.PassbookTemplatesSortableField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

public class PassbookRequestFilter extends BaseRequestFilter {

    private Long entityId;
    private String q;
    private SortOperator<PassbookTemplatesSortableField> sort;
    private Long operatorId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> createDate;
    private Boolean defaultTemplate;
    private PassbookTemplateType type;
    private Long entityAdminId;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<PassbookTemplatesSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<PassbookTemplatesSortableField> sort) {
        this.sort = sort;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getCreateDate() {
        return createDate;
    }

    public void setCreateDate(List<FilterWithOperator<ZonedDateTime>> createDate) {
        this.createDate = createDate;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Boolean getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public PassbookTemplateType getType() {
        return type;
    }

    public void setType(PassbookTemplateType type) {
        this.type = type;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
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
