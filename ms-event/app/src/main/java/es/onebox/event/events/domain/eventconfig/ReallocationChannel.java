package es.onebox.event.events.domain.eventconfig;

import java.io.Serializable;

public class ReallocationChannel implements Serializable {

    private Long id;
    private Boolean applyToAllChannelTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getApplyToAllChannelTypes() {
        return applyToAllChannelTypes;
    }

    public void setApplyToAllChannelTypes(Boolean applyToAllChannelTypes) {
        this.applyToAllChannelTypes = applyToAllChannelTypes;
    }
}
