package es.onebox.mgmt.common;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CommunicationElementTagDTO<T extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = -1731416590204319208L;

    @NotNull(message = "type must not be null")
    private T type;

    @NotNull(message = "backgroundColor must not be null")
    private String backgroundColor;

    @NotNull(message = "textColor must not be null")
    private String textColor;

    @LanguageIETF
    private String language;

    @NotNull(message = "value must not be null")
    private String value;

    private Boolean visible;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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
