package es.onebox.mgmt.common;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CommunicationElementTextDTO <T extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be null")
    private T type;
    @NotNull(message = "language must not be null")
    @LanguageIETF
    private String language;
    @NotNull(message = "value must not be null")
    private String value;

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
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
