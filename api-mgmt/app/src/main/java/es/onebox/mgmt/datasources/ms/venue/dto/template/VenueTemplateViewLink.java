package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateViewLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long viewId;
    private String refId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
