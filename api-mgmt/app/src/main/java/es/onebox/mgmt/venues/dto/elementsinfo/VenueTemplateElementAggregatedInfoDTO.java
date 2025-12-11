package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.dto.TemplatesZonesDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateElementImageTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VenueTemplateElementAggregatedInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8664968287475331772L;

    private Map<String, String> name;
    private Map<String, String> description;
    private VenueTemplateElementRestrictionDTO restriction;
    @JsonProperty("feature_list")
    private Map<String, List<ElementInfoFeatureDTO>> featureList;
    @JsonProperty("config_3D")
    private VenueTemplateElementInfo3DConfigDTO config3D;
    @JsonProperty("image_settings")
    private Map<VenueTemplateElementImageTypeDTO, VenueTemplateElementImageSettingsDTO> imageSettings;
    private VenueTemplateElementBadgeDTO badge;
    @JsonProperty("templates_zones")
    private List<TemplatesZonesDTO> templateZones;
    @JsonProperty("templates_zones_ids")
    private List<Integer> templateZonesIds;


    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, List<ElementInfoFeatureDTO>> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(Map<String, List<ElementInfoFeatureDTO>> featureList) {
        this.featureList = featureList;
    }

    public VenueTemplateElementInfo3DConfigDTO getConfig3D() {
        return config3D;
    }

    public void setConfig3D(VenueTemplateElementInfo3DConfigDTO config3D) {
        this.config3D = config3D;
    }

    public Map<VenueTemplateElementImageTypeDTO, VenueTemplateElementImageSettingsDTO> getImageSettings() {
        return imageSettings;
    }

    public void setImageSettings(Map<VenueTemplateElementImageTypeDTO, VenueTemplateElementImageSettingsDTO> imageSettings) {
        this.imageSettings = imageSettings;
    }

    public VenueTemplateElementBadgeDTO getBadge() {
        return badge;
    }

    public void setBadge(VenueTemplateElementBadgeDTO badge) {
        this.badge = badge;
    }

    public VenueTemplateElementRestrictionDTO getRestriction() {
        return restriction;
    }

    public void setRestriction(VenueTemplateElementRestrictionDTO restriction) {
        this.restriction = restriction;
    }

    public List<TemplatesZonesDTO> getTemplateZones() {
        return templateZones;
    }

    public void setTemplateZones(List<TemplatesZonesDTO> templateZones) {
        this.templateZones = templateZones;
    }

    public List<Integer> getTemplateZonesIds() {
        return templateZonesIds;
    }

    public void setTemplateZonesIds(List<Integer> templateZonesIds) {
        this.templateZonesIds = templateZonesIds;
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
