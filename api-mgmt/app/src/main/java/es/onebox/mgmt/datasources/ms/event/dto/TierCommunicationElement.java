package es.onebox.mgmt.datasources.ms.event.dto;

import es.onebox.mgmt.datasources.common.enums.CommunicationElementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TierCommunicationElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommunicationElementType communicationElementType;
    private String lang;
    private String value;

    public TierCommunicationElement() {}

    public TierCommunicationElement(String lang, String value, CommunicationElementType communicationElementType) {
        this.lang = lang;
        this.value = value;
        this.communicationElementType = communicationElementType;
    }

    public CommunicationElementType getCommunicationElementType() {
        return communicationElementType;
    }

    public void setCommunicationElementType(CommunicationElementType communicationElementType) {
        this.communicationElementType = communicationElementType;
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
