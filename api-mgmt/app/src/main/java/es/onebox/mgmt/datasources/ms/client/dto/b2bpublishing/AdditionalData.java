package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.io.Serializable;

public class AdditionalData extends BaseAdditionalData implements Serializable {

    private String clientName;
    private String username;


    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
