package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TemplateInfoBaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -7606119789588096179L;

    private String id;
    private List<String> tags;
    private AggregatedInfo defaultInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
