package es.onebox.mgmt.products.dto;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductLiteralDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductLiteralDTO() {
    }

    public ProductLiteralDTO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ProductLiteralDTO(String key, String value, Boolean richText, Boolean auditable, String language) {
        this.key = key;
        this.value = value;
        this.richText = richText;
        this.auditable = auditable;
        this.language = language;
    }

    @NotEmpty(message = "key must not be empty")
    @Size(max = 200, message = "key must not be greater than 200 characters")
    private String key;

    private String value;

    private Boolean richText;

    private Boolean auditable;

    @LanguageIETF
    private String language;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRichText() {
        return richText;
    }

    public void setRichText(Boolean richText) {
        this.richText = richText;
    }

    public Boolean getAuditable() {
        return auditable;
    }

    public void setAuditable(Boolean auditable) {
        this.auditable = auditable;
    }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
