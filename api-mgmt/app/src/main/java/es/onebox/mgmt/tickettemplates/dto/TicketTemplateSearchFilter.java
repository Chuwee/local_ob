package es.onebox.mgmt.tickettemplates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.tickettemplates.enums.DesignType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class TicketTemplateSearchFilter extends BaseEntityRequestFilter {

    @Serial
    private static final long serialVersionUID = 233220594309745007L;

    @JsonProperty("design_id")
    private Long designId;
    private TicketTemplateFormat format;
    private String printer;
    @JsonProperty("paper_type")
    private String paperType;
    @JsonProperty("design_type")
    private DesignType designType;

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public TicketTemplateFormat getFormat() {
        return format;
    }

    public void setFormat(TicketTemplateFormat format) {
        this.format = format;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    @JsonProperty("q")
    private String freeSearch;

    private SortOperator<String> sort;

    private List<String> fields;

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

    public DesignType getDesignType() {
        return designType;
    }

    public void setDesignType(DesignType designType) {
        this.designType = designType;
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
