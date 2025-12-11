package es.onebox.mgmt.b2b.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ClientSecretDTO extends IdDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("api_key")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public ClientSecretDTO() {
    }

    public ClientSecretDTO(String apiKey) {
        this.apiKey = apiKey;
    }

    public ClientSecretDTO(String apiKey, Long id) {
        this.apiKey = apiKey;
        super.setId(id);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
