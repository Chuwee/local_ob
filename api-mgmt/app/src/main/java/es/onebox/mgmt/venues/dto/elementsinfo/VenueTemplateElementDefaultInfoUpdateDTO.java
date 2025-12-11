package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueTemplateElementDefaultInfoUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6560492294868871408L;

    private List<String> tags;
    @JsonProperty("default_info")
    private VenueTemplateElementAggregatedInfoDTO defaultInfo;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public VenueTemplateElementAggregatedInfoDTO getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(VenueTemplateElementAggregatedInfoDTO defaultInfo) {
        this.defaultInfo = defaultInfo;
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
