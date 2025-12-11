package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class PasswordConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6048709357590693039L;

    @JsonProperty("max_retries")
    private Long maxRetries;
    private ExpirationDTO expiration;
    private StorageDTO storage;

    public Long getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Long maxRetries) {
        this.maxRetries = maxRetries;
    }

    public StorageDTO getStorage() {
        return storage;
    }

    public void setStorage(StorageDTO storage) {
        this.storage = storage;
    }

    public ExpirationDTO getExpiration() {
        return expiration;
    }

    public void setExpiration(ExpirationDTO expiration) {
        this.expiration = expiration;
    }
}
