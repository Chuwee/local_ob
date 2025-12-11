package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateSessionElementInfoBulkUpdateRequestDTO extends VenueTemplateElementInfoBulkUpdateBaseRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8796539732934381041L;

    @NotNull
    @JsonProperty("element_info")
    private VenueTemplateElementInfoSessionUpdateDTO sessionElementInfo;
    public VenueTemplateElementInfoSessionUpdateDTO getSessionElementInfo() {
        return sessionElementInfo;
    }

    public void setSessionElementInfo(VenueTemplateElementInfoSessionUpdateDTO sessionElementInfo) {
        this.sessionElementInfo = sessionElementInfo;
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
