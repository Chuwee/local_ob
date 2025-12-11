package es.onebox.mgmt.channels.faqs.dto;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelFAQUpsertRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3117927438709370937L;

    @NotEmpty
    private Map<String, ChannelFAQValueDTO> values;
    private List<String> tags;

    public Map<String, ChannelFAQValueDTO> getValues() {
        return values;
    }

    public void setValues(Map<String, ChannelFAQValueDTO> values) {
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
