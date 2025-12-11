package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.publishing.enums.PublishingUserType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class PublisherDataDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    @JsonProperty("user_type")
    private PublishingUserType userType;
    @JsonProperty("client_entity_id")
    private Integer clientEntityId;
    @JsonProperty("client_id")
    private Integer clientId;
    @JsonProperty("client_name")
    private String clientName;
    @JsonProperty("user_id")
    private Integer userId;
    private String username;

    public PublishingUserType getUserType() {
        return userType;
    }
    public void setUserType(PublishingUserType userType) {
        this.userType = userType;
    }

    public Integer getClientEntityId() {
        return clientEntityId;
    }
    public void setClientEntityId(Integer clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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
