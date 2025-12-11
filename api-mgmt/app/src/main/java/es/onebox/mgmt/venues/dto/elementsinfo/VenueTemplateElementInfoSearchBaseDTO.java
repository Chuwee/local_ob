package es.onebox.mgmt.venues.dto.elementsinfo;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.dto.TemplatesZonesDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueTemplateElementInfoSearchBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4471920116517671159L;

    private String id;
    private List<String> tags;
    @JsonProperty("templates_zones")
    private List<TemplatesZonesDTO> templatesZones;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<TemplatesZonesDTO> getTemplatesZones() {
        return templatesZones;
    }

    public void setTemplatesZones(List<TemplatesZonesDTO> templatesZones) {
        this.templatesZones = templatesZones;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
