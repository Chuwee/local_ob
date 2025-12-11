package es.onebox.mgmt.datasources.ms.venue.dto.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateVenueTemplateView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Boolean isRoot;
    private Boolean vip;
    private Boolean aggregatedView;
    private Boolean display3D;
    private VenueTemplateViewOrientation orientation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
