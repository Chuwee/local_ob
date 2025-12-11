package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatReallocationChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "channel id cannot be null")
    private Long id;

    @NotNull(message = "apply_to_all_channel_types cannot be null")
    @JsonProperty("apply_to_all_channel_types")
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
