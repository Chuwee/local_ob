package es.onebox.mgmt.venues.dto.elementsinfo;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateItemElementInfoDTO extends VenueTemplateItemElementInfoBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -432175320064664626L;

    private VenueTemplateElementInfoSearchBaseDTO element;

    public VenueTemplateElementInfoSearchBaseDTO getElement() {
        return element;
    }

    public void setElement(VenueTemplateElementInfoSearchBaseDTO element) {
        this.element = element;
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
