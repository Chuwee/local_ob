package es.onebox.common.auth.dto;

import es.onebox.common.datasources.ms.entity.enums.EntityType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private Long entityId;
    private Long operatorId;
    private List<EntityType> entityTypes;
    private String operatorTimeZone;
    private Long userClientId;
    private Long clientEntityId;
    private Long channelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(List<EntityType> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public String getOperatorTimeZone() {
		return operatorTimeZone;
	}

	public void setOperatorTimeZone(String operatorTimeZone) {
		this.operatorTimeZone = operatorTimeZone;
	}

    public Long getUserClientId() {
        return userClientId;
    }

    public void setUserClientId(Long userClientId) {
        this.userClientId = userClientId;
    }

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Long clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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
