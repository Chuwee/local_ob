package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class Dates implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

}
