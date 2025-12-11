package es.onebox.mgmt.events.tags.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionTagRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull(message = "enabled cannot be null")
    private Boolean enabled;
    @Pattern(regexp = "^([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$", message = "Invalid characters. Color must be in hexadecimal format")
    @NotNull(message = "backgroundColor cannot be null")
    @JsonProperty("background_color")
    private String backgroundColor;
    @Pattern(regexp = "^([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$", message = "Invalid characters. Color must be in hexadecimal format")
    @NotNull(message = "textColor cannot be null")
    @JsonProperty("text_color")
    private String textColor;
    @Valid
    @NotNull(message = "channels cannot be null")
    private ChannelsSessionTagsRequestDTO channels;
    @Valid
    @NotEmpty(message = "languages cannot be empty")
    private List<SessionTagLanguageDTO> languages;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public ChannelsSessionTagsRequestDTO getChannels() {
        return channels;
    }

    public void setChannels(ChannelsSessionTagsRequestDTO channels) {
        this.channels = channels;
    }

    public List<SessionTagLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<SessionTagLanguageDTO> languages) {
        this.languages = languages;
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
