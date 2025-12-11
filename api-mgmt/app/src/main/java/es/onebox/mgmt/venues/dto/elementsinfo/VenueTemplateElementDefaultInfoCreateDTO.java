package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class VenueTemplateElementDefaultInfoCreateDTO extends VenueTemplateElementDefaultInfoBaseCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5997188415836576636L;

    @JsonProperty("copy_info")
    private VenueTemplateElementCopyInfo copyInfo;

    public VenueTemplateElementCopyInfo getCopyInfo() {
        return copyInfo;
    }

    public void setCopyInfo(VenueTemplateElementCopyInfo copyInfo) {
        this.copyInfo = copyInfo;
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
