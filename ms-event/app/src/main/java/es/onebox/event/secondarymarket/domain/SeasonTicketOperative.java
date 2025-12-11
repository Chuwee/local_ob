package es.onebox.event.secondarymarket.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketOperative implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private ZonedDateTime saleStartDate;
    private ZonedDateTime saleEndDate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(ZonedDateTime saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public ZonedDateTime getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(ZonedDateTime saleEndDate) {
        this.saleEndDate = saleEndDate;
    }
}
