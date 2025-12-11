package es.onebox.event.surcharges.product.dto;

import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.product.enums.ProductSurchargeType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSurchargeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull(message = "Type is mandatory")
    private ProductSurchargeType type;
    private List<RangeDTO> ranges;

    public ProductSurchargeDTO() {
    }

    public ProductSurchargeDTO(ProductSurchargeType type, List<RangeDTO> ranges) {
        this.type = type;
        this.ranges = ranges;
    }

    public ProductSurchargeType getType() {
        return type;
    }

    public void setType(ProductSurchargeType type) {
        this.type = type;
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
