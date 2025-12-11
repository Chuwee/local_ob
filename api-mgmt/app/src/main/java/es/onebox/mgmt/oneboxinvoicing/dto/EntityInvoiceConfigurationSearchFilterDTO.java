package es.onebox.mgmt.oneboxinvoicing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;

import java.util.List;

public class EntityInvoiceConfigurationSearchFilterDTO {

    @JsonProperty("sort")
    private SortOperator<String> sort;

    @JsonProperty("q")
    private String freeSearch;

    @JsonProperty("type")
    private List<OneboxInvoiceType> type;

    public EntityInvoiceConfigurationSearchFilterDTO() {
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public List<OneboxInvoiceType> getType() {
        return type;
    }

    public void setType(List<OneboxInvoiceType> type) {
        this.type = type;
    }
}
