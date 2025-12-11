package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationPurchaseElementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PurchaseCommunicationElementDTO extends IdDTO {

    private static final long serialVersionUID = 1L;

    private String language;
    private String urlImage;
    private String value;
    private CommunicationPurchaseElementType type;
    private String altText;

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

    public CommunicationPurchaseElementType getType() {
        return type;
    }

    public void setType(CommunicationPurchaseElementType type) {
        this.type = type;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
