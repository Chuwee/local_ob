package es.onebox.mgmt.channels.contents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateChannelTextBlockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id must not be null")
    private Long id;
    @LanguageIETF
    @NotBlank(message = "language must not be empty")
    private String language;
    private String subject;
    private String value;
    @JsonProperty("use_free_text")
    private Boolean useFreeText;

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

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Boolean getUseFreeText() {
        return useFreeText;
    }

    public void setUseFreeText(Boolean useFreeText) {
        this.useFreeText = useFreeText;
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
