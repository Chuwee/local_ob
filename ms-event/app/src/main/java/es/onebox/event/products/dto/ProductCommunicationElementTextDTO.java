package es.onebox.event.products.dto;

import es.onebox.event.products.enums.ProductCommunicationElementTextsType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductCommunicationElementTextDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ProductCommunicationElementTextsType type;
    private Long languageId;
    private String language;
    private String value;

    public ProductCommunicationElementTextsType getType() {
        return type;
    }

    public void setType(ProductCommunicationElementTextsType type) {
        this.type = type;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

