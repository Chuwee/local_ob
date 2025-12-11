package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.VenueTemplateViewOrientation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateVenueTemplateViewBulkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id must not be null")
    @Min(value = 1, message = "id must be above 0")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("vip")
    private Boolean vip;

    @JsonProperty("aggregated_view")
    private Boolean aggregatedView;

    @JsonProperty("display_3D")
    private Boolean display3D;

    @JsonProperty("orientation")
    private VenueTemplateViewOrientation orientation;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
