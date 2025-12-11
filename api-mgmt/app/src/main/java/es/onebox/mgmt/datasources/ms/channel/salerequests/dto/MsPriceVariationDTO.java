package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;


import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsPriceVariationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class MsPriceVariationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private MsPriceVariationType type;
    private Double value;
    private List<MsRangeDTO> ranges;

    public MsPriceVariationType getType() {
        return type;
    }

    public void setType(MsPriceVariationType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<MsRangeDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<MsRangeDTO> ranges) {
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
