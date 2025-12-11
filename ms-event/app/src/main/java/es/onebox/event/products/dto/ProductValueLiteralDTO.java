package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductValueLiteralDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long valueId;
    private String key;
    private String value;
    private Boolean richText;
    private Boolean auditable;
    private String languageCode;

    public ProductValueLiteralDTO(Long valueId, String key, String value, String languageCode) {
        this.valueId = valueId;
        this.key = key;
        this.value = value;
        this.languageCode = languageCode;
    }

    public ProductValueLiteralDTO() {
    }

    public Long getValueId() { return valueId; }

    public void setValueId(Long valueId) { this.valueId = valueId; }

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

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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
