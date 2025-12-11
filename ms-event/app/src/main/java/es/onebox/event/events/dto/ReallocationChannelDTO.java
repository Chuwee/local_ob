package es.onebox.event.events.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ReallocationChannelDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
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
