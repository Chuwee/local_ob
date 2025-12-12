package es.onebox.common.datasources.ms.crm.dto;

import java.io.Serializable;

public class ChannelAgreement implements Serializable {

    private static final long serialVersionUID = -6134286966326931924L;

    private String name;
    private Boolean accepted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
