package es.onebox.event.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PriceTypeCommunicationElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommunicationElementType webCommunicationElementType;
    private String lang;
    private String value;

    public PriceTypeCommunicationElement() {}

    public PriceTypeCommunicationElement(String lang, String value, CommunicationElementType webCommunicationElementType) {
        this.lang = lang;
        this.value = value;
        this.webCommunicationElementType = webCommunicationElementType;
    }

    public CommunicationElementType getWebCommunicationElementType() {
        return webCommunicationElementType;
    }

    public void setWebCommunicationElementType(CommunicationElementType webCommunicationElementType) {
        this.webCommunicationElementType = webCommunicationElementType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
