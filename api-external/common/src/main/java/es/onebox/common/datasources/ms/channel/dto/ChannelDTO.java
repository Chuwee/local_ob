package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.event.enums.ChannelSubtype;
import es.onebox.common.datasources.ms.event.enums.ChannelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long entityId;
    private String entityName;
    private Long operatorId;
    private String externalReference;
    private WhitelabelType whitelabelType;

    private ChannelType type;
    private ChannelSubtype subtype;
    private ChannelStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public ChannelSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelSubtype subtype) {
        this.subtype = subtype;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
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
