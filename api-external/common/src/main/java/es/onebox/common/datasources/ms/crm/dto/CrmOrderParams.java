package es.onebox.common.datasources.ms.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrmOrderParams {

    private String id;

    private String status;

    @JsonProperty("client_id")
    private Long clientId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
