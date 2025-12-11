package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class PromotionValidityDates implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime start;
    private ZonedDateTime end;

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }
}
