package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long entityId;
    private String entityName;
    private String entityLogo;
    private ChannelType type;
    private ChannelSubtype subtype;
    private ChannelStatus status;
    private Long operatorId;
    private String operatorName;
    private String url;

    public ChannelSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelSubtype subtype) {
        this.subtype = subtype;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityLogo() {
        return entityLogo;
    }

    public void setEntityLogo(String entityLogo) {
        this.entityLogo = entityLogo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
