package es.onebox.mgmt.venues.dto.elementsinfo;

import es.onebox.mgmt.validation.annotation.UrlFormat;
import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureAction;
import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class ElementInfoFeatureDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5245161202974638256L;

    @NotNull
    private VenueTemplateElementFeatureType type;
    @NotNull
    private String text;
    @UrlFormat
    private String url;
    private VenueTemplateElementFeatureAction action;

    public ElementInfoFeatureDTO() {
    }

    public ElementInfoFeatureDTO(VenueTemplateElementFeatureType type, String text, String url, VenueTemplateElementFeatureAction action) {
        this.type = type;
        this.text = text;
        this.url = url;
        this.action = action;
    }

    public VenueTemplateElementFeatureType getType() {
        return type;
    }

    public void setType(VenueTemplateElementFeatureType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VenueTemplateElementFeatureAction getAction() {
        return action;
    }

    public void setAction(VenueTemplateElementFeatureAction action) {
        this.action = action;
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
