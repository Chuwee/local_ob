package es.onebox.mgmt.entities.contents.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateEntityTextBlocks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private List<UpdateEntityTextBlock> values;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UpdateEntityTextBlock> getValues() {
        return values;
    }

    public void setValues(List<UpdateEntityTextBlock> values) {
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
