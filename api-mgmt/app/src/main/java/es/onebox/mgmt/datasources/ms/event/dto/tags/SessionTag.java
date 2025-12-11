package es.onebox.mgmt.datasources.ms.event.dto.tags;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SessionTag implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer position;
    private Boolean enabled;
    private String backgroundColor;
    private String textColor;
    private ChannelsSessionTags channels;
    private List<SessionTagLanguage> languages;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

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

    public ChannelsSessionTags getChannels() {
        return channels;
    }

    public void setChannels(ChannelsSessionTags channels) {
        this.channels = channels;
    }

    public List<SessionTagLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<SessionTagLanguage> languages) {
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
