package es.onebox.mgmt.salerequests.dto;

import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseSessionRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SearchSaleRequestSessionsFilter extends BaseSessionRequestFilter {

    private static final long serialVersionUID = -2983764828181435750L;

    private SortOperator<String> sort;
    private boolean published;

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public boolean getPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
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
