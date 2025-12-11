package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatReallocationChannel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
