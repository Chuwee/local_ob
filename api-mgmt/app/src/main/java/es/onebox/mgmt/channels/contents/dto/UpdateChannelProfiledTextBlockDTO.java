package es.onebox.mgmt.channels.contents.dto;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateChannelProfiledTextBlockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id must not be null")
    private Long id;
    @LanguageIETF
    @NotBlank(message = "language must not be empty")
    private String language;
    private String value;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
