package es.onebox.mgmt.venues.dto.elementsinfo;

import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.TemplateInfoStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class VenueTemplateElementInfoSessionResponseDTO extends VenueTemplateElementInfoDefaultResponseDTO {

    @Serial
    private static final long serialVersionUID = 7131190197455477164L;

    private TemplateInfoStatus status;

    public TemplateInfoStatus getStatus() {
        return status;
    }

    public void setStatus(TemplateInfoStatus status) {
        this.status = status;
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
