package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.request.SortOperator;

import java.io.Serializable;
import java.util.List;

public class EntityInvoiceConfigurationSearchFilter implements Serializable {

    private SortOperator<String> sort;
    private String freeSearch;
    private List<OneboxInvoiceType> type;

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
