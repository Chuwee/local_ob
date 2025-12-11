package es.onebox.mgmt.events.tags.dto;

import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class SessionTagLanguageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "language cannot be blank")
    private String language;
    @NotBlank(message = "text cannot be blank")
    @Length(max = 30, message = "text max size is 30")
    private String text;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
