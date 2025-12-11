package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.venues.enums.VenueTagStatus;

import java.io.Serializable;

public class VenueTagStatusCounterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String source;
    private VenueTagStatus status;
    private Integer count;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public VenueTagStatus getStatus() {
        return status;
    }

    public void setStatus(VenueTagStatus status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
