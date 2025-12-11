package es.onebox.mgmt.datasources.ms.venue.dto.template;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class VenueTemplateView extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 2L;

    private String description;
    private String url;
    private Boolean isRoot;
    private Boolean vip;
    private Boolean aggregatedView;
    private Boolean display3D;
    private VenueTemplateViewOrientation orientation;
    private List<VenueTemplateViewLink> links;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    public Boolean getAggregatedView() {
        return aggregatedView;
    }

    public void setAggregatedView(Boolean aggregatedView) {
        this.aggregatedView = aggregatedView;
    }

    public Boolean getDisplay3D() {
        return display3D;
    }

    public void setDisplay3D(Boolean display3D) {
        this.display3D = display3D;
    }

    public VenueTemplateViewOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(VenueTemplateViewOrientation orientation) {
        this.orientation = orientation;
    }

    public List<VenueTemplateViewLink> getLinks() {
        return links;
    }

    public void setLinks(List<VenueTemplateViewLink> links) {
        this.links = links;
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
