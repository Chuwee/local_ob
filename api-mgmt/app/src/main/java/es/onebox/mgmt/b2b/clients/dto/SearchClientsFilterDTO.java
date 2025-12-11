package es.onebox.mgmt.b2b.clients.dto;

import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SearchClientsFilterDTO extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    private String q;
    private SortOperator<String> sort;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
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
