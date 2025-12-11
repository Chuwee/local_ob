package es.onebox.mgmt.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import es.onebox.mgmt.validation.annotation.UrlFormat;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CommunicationElementUrlDTO<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type cannot be null")
    private T type;

    @LanguageIETF
    @NotNull(message = "language cannot be null")
    private String language;

    @JsonProperty("redirect_url")
    @UrlFormat
    @NotNull(message = "url cannot be null")
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
