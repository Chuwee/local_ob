package es.onebox.mgmt.passbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

public class PassbookRequestFilterDTO extends BaseEntityRequestFilter {

    private String q;
    private SortOperator<PassbookTemplatesSortableField> sort;
    @JsonProperty("create_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> createDate;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("default_template")
    private Boolean defaultTemplate;
    private PassbookTemplateType type;


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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getCreateDate() {
        return createDate;
    }

    public void setCreateDate(List<FilterWithOperator<ZonedDateTime>> createDate) {
        this.createDate = createDate;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
