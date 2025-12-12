package es.onebox.common.datasources.ms.ticket.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionTicketSearchFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -4651370066037215635L;
    private List<TicketField> fields;
    private SortOperator<TicketSortableField> sort;

    public SessionTicketSearchFilter() {
    }

    public List<TicketField> getFields() {
        return fields;
    }

    public void setFields(List<TicketField> fields) {
        this.fields = fields;
    }

    public SortOperator<TicketSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<TicketSortableField> sort) {
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
