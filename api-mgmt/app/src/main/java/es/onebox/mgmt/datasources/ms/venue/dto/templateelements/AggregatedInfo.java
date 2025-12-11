package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.venues.dto.TemplatesZonesDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementRestrictionDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AggregatedInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8353503022266920000L;

    private Map<String, String> name;
    private Map<String, String> description;
    private VenueTemplateElementRestrictionDTO restriction;
    private Map<String, List<Feature>> featureList;
    private TemplateInfo3DConfig config3D;
    private Map<ElementInfoImageType, ImageSettings> imageSettings;
    private Badge badge;
    private List<Integer> templatesZonesIds;
    private List<TemplatesZonesDTO> templatesZones;

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

    public Map<String, List<Feature>> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(Map<String, List<Feature>> featureList) {
        this.featureList = featureList;
    }

    public TemplateInfo3DConfig getConfig3D() {
        return config3D;
    }

    public void setConfig3D(TemplateInfo3DConfig config3D) {
        this.config3D = config3D;
    }

    public Map<ElementInfoImageType, ImageSettings> getImageSettings() {
        return imageSettings;
    }

    public void setImageSettings(Map<ElementInfoImageType, ImageSettings> imageSettings) {
        this.imageSettings = imageSettings;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public VenueTemplateElementRestrictionDTO getRestriction() {
        return restriction;
    }

    public void setRestriction(VenueTemplateElementRestrictionDTO restriction) {
        this.restriction = restriction;
    }

    public List<Integer> getTemplatesZonesIds() {
        return templatesZonesIds;
    }

    public void setTemplatesZonesIds(List<Integer> templatesZonesIds) {
        this.templatesZonesIds = templatesZonesIds;
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
