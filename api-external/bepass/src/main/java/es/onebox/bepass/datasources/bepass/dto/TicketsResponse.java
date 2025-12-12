package es.onebox.bepass.datasources.bepass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TicketsResponse implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Long page;
    @JsonProperty("page_size")
    private Long pageSize;
    private List<TicketExtended> tickets;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<TicketExtended> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketExtended> tickets) {
        this.tickets = tickets;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
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
