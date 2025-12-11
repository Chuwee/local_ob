package es.onebox.mgmt.venues.dto.elementsinfo;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.venues.enums.VenueTemplateElementInfoItemType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

@MaxLimit(50)
public class VenueTemplateElementInfoSearchDTO extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -2773280381393447947L;

    private String q;
    private VenueTemplateElementInfoItemType type;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public VenueTemplateElementInfoItemType getType() {
        return type;
    }

    public void setType(VenueTemplateElementInfoItemType type) {
        this.type = type;
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
