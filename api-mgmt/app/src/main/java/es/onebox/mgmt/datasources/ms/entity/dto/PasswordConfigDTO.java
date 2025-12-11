package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class PasswordConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5548434271483009244L;

    private Long maxRetries;
    private ExpirationDTO expiration;
    private StorageDTO storage;

    public Long getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Long maxRetries) {
        this.maxRetries = maxRetries;
    }

    public ExpirationDTO getExpiration() {
        return expiration;
    }

    public void setExpiration(ExpirationDTO expiration) {
        this.expiration = expiration;
    }

    public StorageDTO getStorage() {
        return storage;
    }

    public void setStorage(StorageDTO storage) {
        this.storage = storage;
    }
}
