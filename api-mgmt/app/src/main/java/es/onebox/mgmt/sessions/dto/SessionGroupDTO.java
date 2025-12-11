package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.groups.GroupDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SessionGroupDTO extends GroupDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("use_venue_template_group_config")
    private Boolean useVenueTemplateGroupConfig;

    @JsonProperty("venue_template_name")
    private String venueTemplateName;

    public Boolean getUseVenueTemplateGroupConfig() {
        return useVenueTemplateGroupConfig;
    }

    public void setUseVenueTemplateGroupConfig(Boolean useVenueTemplateGroupConfig) {
        this.useVenueTemplateGroupConfig = useVenueTemplateGroupConfig;
    }

    public String getVenueTemplateName() {
        return venueTemplateName;
    }

    public void setVenueTemplateName(String venueTemplateName) {
        this.venueTemplateName = venueTemplateName;
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
