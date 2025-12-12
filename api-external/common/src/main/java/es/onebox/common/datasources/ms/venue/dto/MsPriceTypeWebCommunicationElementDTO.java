package es.onebox.common.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MsPriceTypeWebCommunicationElementDTO implements Serializable  {

    @Serial
    private static final long serialVersionUID = -7621717351277903472L;

    private String webCommunicationElementType;
    private String lang;
    private String value;

    public String getType() {
        return webCommunicationElementType;
    }

    public void setType(String type) {
        this.webCommunicationElementType = type;
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
