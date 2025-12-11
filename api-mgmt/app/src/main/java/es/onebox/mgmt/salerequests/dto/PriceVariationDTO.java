package es.onebox.mgmt.salerequests.dto;

import es.onebox.mgmt.salerequests.enums.PriceVariationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class PriceVariationDTO implements Serializable {
    private static final long serialVersionUID = 2731598720156259520L;

    private PriceVariationType type;
    private Double value;
    private List<RangeDTO> ranges;

    public PriceVariationType getType() {
        return type;
    }

    public void setType(PriceVariationType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<RangeDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeDTO> ranges) {
        this.ranges = ranges;
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
