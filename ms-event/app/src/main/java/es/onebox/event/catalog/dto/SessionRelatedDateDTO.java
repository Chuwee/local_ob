package es.onebox.event.catalog.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class SessionRelatedDateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8420230491356788792L;

    private ZonedDateTime beginSessionDate;
    private Boolean showDate;
    private Boolean showDateTime;


    public SessionRelatedDateDTO() {
    }

    public SessionRelatedDateDTO(ZonedDateTime beginSessionDate, Boolean showDate, Boolean showDateTime) {
        this.beginSessionDate = beginSessionDate;
        this.showDate = showDate;
        this.showDateTime = showDateTime;
    }

    public ZonedDateTime getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(ZonedDateTime beginSessionDate) {
        this.beginSessionDate = beginSessionDate;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }
}
