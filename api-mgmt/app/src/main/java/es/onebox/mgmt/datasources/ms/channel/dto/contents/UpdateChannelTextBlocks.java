package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateChannelTextBlocks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private List<UpdateChannelTextBlock> values;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UpdateChannelTextBlock> getValues() {
        return values;
    }

    public void setValues(List<UpdateChannelTextBlock> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
