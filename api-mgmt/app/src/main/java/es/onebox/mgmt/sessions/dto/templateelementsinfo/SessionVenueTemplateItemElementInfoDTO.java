package es.onebox.mgmt.sessions.dto.templateelementsinfo;

import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateItemElementInfoBaseDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class SessionVenueTemplateItemElementInfoDTO extends VenueTemplateItemElementInfoBaseDTO {

    @Serial
    private static final long serialVersionUID = 3149301143345365011L;

    private SessionVenueTemplateElementInfoSearchBaseDTO element;

    public SessionVenueTemplateElementInfoSearchBaseDTO getElement() {
        return element;
    }

    public void setElement(SessionVenueTemplateElementInfoSearchBaseDTO element) {
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
