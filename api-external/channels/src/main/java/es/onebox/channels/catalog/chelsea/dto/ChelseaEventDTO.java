package es.onebox.channels.catalog.chelsea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.ChannelEventCategory;
import es.onebox.common.datasources.catalog.dto.ChannelEventImages;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ChelseaEventDTO implements Serializable {

    private static final long serialVersionUID = 6404355482133225518L;

    private Long id;
    private String name;
    private String reference;
    private Map<String, String> url;
    private Map<String, String> title;
    @JsonProperty("description_short")
    private Map<String, String> shortDescription;
    @JsonProperty("description_long")
    private Map<String, String> longDescription;
    private ChannelEventImages image;
    private ChannelEventCategory category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Map<String, String> shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Map<String, String> getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(Map<String, String> longDescription) {
        this.longDescription = longDescription;
    }

    public ChannelEventImages getImage() {
        return image;
    }

    public void setImage(ChannelEventImages image) {
        this.image = image;
    }

    public ChannelEventCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelEventCategory category) {
        this.category = category;
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
