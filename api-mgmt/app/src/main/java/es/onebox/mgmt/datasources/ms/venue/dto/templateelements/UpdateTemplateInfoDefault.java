package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateTemplateInfoDefault implements Serializable {

    @Serial
    private static final long serialVersionUID = -4550720569551887457L;

    private List<String> tags;
    private AggregatedInfo defaultInfo;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public AggregatedInfo getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(AggregatedInfo defaultInfo) {
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
