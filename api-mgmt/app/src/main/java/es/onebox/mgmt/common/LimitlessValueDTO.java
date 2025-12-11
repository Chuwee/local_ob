package es.onebox.mgmt.common;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class LimitlessValueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LimitlessValueType type;
    private Long value;

    public LimitlessValueDTO() {
    }

    public LimitlessValueDTO(Integer value) {
        this(value != null ? value.longValue() : null);
    }

    public LimitlessValueDTO(Long value) {
        if (value == null || value < 0) {
            this.type = LimitlessValueType.UNLIMITED;
        } else {
            this.type = LimitlessValueType.FIXED;
            this.value = value;
        }
    }

    public LimitlessValueType getType() {
        return type;
    }

    public void setType(LimitlessValueType type) {
        this.type = type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
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
