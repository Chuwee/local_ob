package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class TicketCodeValidity implements Serializable {

    @Serial
    private static final long serialVersionUID = 3811974807138929880L;

    @JsonProperty("expiry_date")
    private ZonedDateTime expiryDate;
    @JsonProperty("remaining_uses")
    private Integer remainingUses;

    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getRemainingUses() {
        return remainingUses;
    }

    public void setRemainingUses(Integer remainingUses) {
        this.remainingUses = remainingUses;
    }
}
