package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypeMappingRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("source_price_type_id")
    @Min(value = 0, message = "price_type_id must be greater than or equal to 0")
    private Integer sourcePriceTypeId;

    @JsonProperty("target_price_type_id")
    @Min(value = 0, message = "price_type_id must be greater than or equal to 0")
    private List<Integer> targetPriceTypeId;

    public Integer getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(Integer sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public List<Integer> getTargetPriceTypeId() {
        return targetPriceTypeId;
    }

    public void setTargetPriceTypeId(List<Integer> targetPriceTypeId) {
        this.targetPriceTypeId = targetPriceTypeId;
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
