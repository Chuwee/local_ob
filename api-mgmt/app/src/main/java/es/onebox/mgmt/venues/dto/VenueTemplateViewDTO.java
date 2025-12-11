package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class VenueTemplateViewDTO extends IdNameDTO {

    private static final long serialVersionUID = 2L;

    @JsonProperty("code")
    private String code;
    @JsonProperty("url")
    private String url;
    @JsonProperty("root")
    private boolean isRoot;
    @JsonProperty("vip")
    private Boolean vip;
    @JsonProperty("aggregated_view")
    private Boolean aggregatedView;
    @JsonProperty("display_3D")
    private Boolean display3D;
    @JsonProperty("orientation")
    private VenueTemplateViewOrientation orientation;
    @JsonProperty("links")
    private List<ViewLinkDTO> links;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
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

    public List<ViewLinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<ViewLinkDTO> links) {
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
