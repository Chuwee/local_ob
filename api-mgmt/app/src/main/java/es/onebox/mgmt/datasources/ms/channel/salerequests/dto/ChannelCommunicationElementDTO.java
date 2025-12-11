package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationChannelElementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelCommunicationElementDTO extends IdDTO {

    private static final long serialVersionUID = 1L;

    private String language;
    private String urlImage;
    private String value;
    private CommunicationChannelElementType type;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CommunicationChannelElementType getType() {
        return type;
    }

    public void setType(CommunicationChannelElementType type) {
        this.type = type;
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
