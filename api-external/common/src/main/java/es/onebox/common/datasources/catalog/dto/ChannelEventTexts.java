package es.onebox.common.datasources.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class ChannelEventTexts implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<String, String> title;
    private Map<String, String> subtitle;
    @JsonProperty("description_short")
    private Map<String, String> descriptionShort;
    @JsonProperty("description_long")
    private Map<String, String> descriptionLong;
    private Map<String, String> duration;

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Map<String, String> subtitle) {
        this.subtitle = subtitle;
    }

    public Map<String, String> getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(Map<String, String> descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public Map<String, String> getDescriptionLong() {
        return descriptionLong;
    }

    public void setDescriptionLong(Map<String, String> descriptionLong) {
        this.descriptionLong = descriptionLong;
    }

    public Map<String, String> getDuration() {
        return duration;
    }

    public void setDuration(Map<String, String> duration) {
        this.duration = duration;
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
