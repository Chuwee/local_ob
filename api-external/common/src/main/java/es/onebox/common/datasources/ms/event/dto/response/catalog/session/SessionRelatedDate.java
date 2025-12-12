package es.onebox.common.datasources.ms.event.dto.response.catalog.session;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class SessionRelatedDate implements Serializable {
    @Serial
    private static final long serialVersionUID = -7947012468064013550L;
    private Date beginSessionDate;
    private Boolean showDate;
    private Boolean showDateTime;

    public SessionRelatedDate(){

    }

    public SessionRelatedDate(Date beginSessionDate, Boolean showDate, Boolean showDateTime) {
        this.beginSessionDate = beginSessionDate;
        this.showDate = showDate;
        this.showDateTime = showDateTime;
    }

    public Date getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(Date beginSessionDate) {
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
