package es.onebox.mgmt.datasources.ms.channel.dto.faqs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelFAQUpsertRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3117927438709370937L;

    private Map<String, ChannelFAQValue> values;
    private List<String> tags;

    public Map<String, ChannelFAQValue> getValues() {
        return values;
    }

    public void setValues(Map<String, ChannelFAQValue> values) {
        this.values = values;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
