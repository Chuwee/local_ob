package es.onebox.mgmt.common.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class GroupCompanionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer min;
    private LimitlessValueDTO max;
    @JsonProperty("occupy_capacity")
    private Boolean occupyCapacity;

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public LimitlessValueDTO getMax() {
        return max;
    }

    public void setMax(LimitlessValueDTO max) {
        this.max = max;
    }

    public Boolean getOccupyCapacity() {
        return occupyCapacity;
    }

    public void setOccupyCapacity(Boolean occupyCapacity) {
        this.occupyCapacity = occupyCapacity;
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
