package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.dto.RangeDTO;
import es.onebox.common.datasources.ms.event.enums.ProductSurchargeType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductSurchargeFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2345678901234567890L;

    private ProductSurchargeType type;
    private List<RangeDTO> ranges;

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
