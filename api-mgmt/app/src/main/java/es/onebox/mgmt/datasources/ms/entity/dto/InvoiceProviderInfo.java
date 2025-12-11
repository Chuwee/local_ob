package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class InvoiceProviderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long producerId;
    private String provider;
    private String status;

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
