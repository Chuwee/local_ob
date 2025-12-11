package es.onebox.mgmt.products.surcharges.dto;

import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.products.surcharges.enums.ProductSurchargeType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSurchargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private ProductSurchargeType type;
    private List<RangeDTO> ranges;

    public ProductSurchargeDTO() {
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
}