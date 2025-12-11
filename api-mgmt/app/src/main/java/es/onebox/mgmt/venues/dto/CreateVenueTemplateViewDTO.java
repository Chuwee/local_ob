package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CreateVenueTemplateViewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "name must not be blank")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "code must not be blank")
    @JsonProperty("code")
    private String code;

    @NotNull(message = "root must not be null")
    @JsonProperty("root")
    private Boolean isRoot;

    @JsonProperty("vip")
    private Boolean vip;

    @JsonProperty("aggregated_view")
    private Boolean aggregatedView;

    @JsonProperty("display_3D")
    private Boolean display3D;

    @JsonProperty("orientation")
    private VenueTemplateViewOrientation orientation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
