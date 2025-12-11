package es.onebox.mgmt.sessions.dto.templateelementsinfo;

import es.onebox.core.serializer.dto.response.Metadata;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionVenueTemplateElementInfoSearchResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1787165068218866304L;

    private Metadata metadata;
    private List<SessionVenueTemplateItemElementInfoDTO> data;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<SessionVenueTemplateItemElementInfoDTO> getData() {
        return data;
    }

    public void setData(List<SessionVenueTemplateItemElementInfoDTO> data) {
        this.data = data;
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
