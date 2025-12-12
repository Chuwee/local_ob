package es.onebox.atm.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class TicketURLContentDTO {

    @JsonProperty("has_additional_tickets")
    private Boolean hasAdditionalTickets;
    private String url;

    private List<String> tickets;

    public TicketURLContentDTO(Boolean hasAdditionalTickets) {
        this.hasAdditionalTickets = hasAdditionalTickets;
    }

    public TicketURLContentDTO(Boolean hasAdditionalTickets, String url, List<String> tickets) {
        this.hasAdditionalTickets = hasAdditionalTickets;
        this.url = url;
        this.tickets = tickets;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasAdditionalTickets() {
        return hasAdditionalTickets;
    }

    public void setHasAdditionalTickets(Boolean hasAdditionalTickets) {
        this.hasAdditionalTickets = hasAdditionalTickets;
    }

    public List<String> getTickets() {
        return tickets;
    }

    public void setTickets(List<String> tickets) {
        this.tickets = tickets;
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
