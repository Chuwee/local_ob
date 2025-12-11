package es.onebox.event.seasontickets.amqp.session;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.util.List;

public class CreateSeasonTicketSessionMessage extends AbstractNotificationMessage {

    private static final long serialVersionUID = 14534343L;

    private Long seasonTicketId;
    private String name;
    private Long venueConfigId;
    private Long taxId;
    private Long chargeTaxId;
    private List<Long> ticketTaxIds;
    private List<Long> chargeTaxIds;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Long getChargeTaxId() {
        return chargeTaxId;
    }

    public void setChargeTaxId(Long chargeTaxId) {
        this.chargeTaxId = chargeTaxId;
    }

    public List<Long> getTicketTaxIds() {
        return ticketTaxIds;
    }

    public void setTicketTaxIds(List<Long> ticketTaxIds) {
        this.ticketTaxIds = ticketTaxIds;
    }

    public List<Long> getChargeTaxIds() {
        return chargeTaxIds;
    }

    public void setChargeTaxIds(List<Long> chargeTaxIds) {
        this.chargeTaxIds = chargeTaxIds;
    }

}
