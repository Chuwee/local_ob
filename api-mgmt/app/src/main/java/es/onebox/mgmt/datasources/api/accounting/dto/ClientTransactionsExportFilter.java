package es.onebox.mgmt.datasources.api.accounting.dto;

import es.onebox.mgmt.export.dto.ExportFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClientTransactionsExportFilter extends ExportFilter<ClientTransactionsExportFileFieldDTO> {

    private SearchTransactionsFilter filter;

    public SearchTransactionsFilter getFilter() {
        return filter;
    }

    public void setFilter(SearchTransactionsFilter filter) {
        this.filter = filter;
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
