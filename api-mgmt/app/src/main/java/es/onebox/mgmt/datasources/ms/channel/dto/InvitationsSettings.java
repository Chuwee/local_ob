package es.onebox.mgmt.datasources.ms.channel.dto;


import es.onebox.mgmt.channels.enums.InvitationsSelectionMode;

import java.io.Serializable;

public class InvitationsSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private InvitationsSelectionMode selectionMode;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public InvitationsSelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(InvitationsSelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }
}
