package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.surcharges.enums.ProductSurchargeType;
import es.onebox.mgmt.datasources.common.dto.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSurcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ProductSurchargeType type;
    private List<Range> ranges;

    public ProductSurchargeType getType() {
        return type;
    }

    public void setType(ProductSurchargeType type) {
        this.type = type;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }
}

