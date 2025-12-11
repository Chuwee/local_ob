package es.onebox.event.datasources.ms.ticket.dto.secondarymarket;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;


public class SecondaryMarketSearch implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SecondaryMarketStatus status;
    private ZonedDateTime creationDate;
    private ZonedDateTime updateDate;
    private String currencyCode;
    private Double commission;
    private Ticket ticket;

    public SecondaryMarketStatus getStatus() {
        return status;
    }

    public void setStatus(SecondaryMarketStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(ZonedDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
