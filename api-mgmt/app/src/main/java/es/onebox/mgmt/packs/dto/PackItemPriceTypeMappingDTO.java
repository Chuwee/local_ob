package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PackItemPriceTypeMappingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("source_price_type")
    private IdNameDTO sourcePriceTypeId;

    @JsonProperty("target_price_type")
    @Min(value = 0, message = "price_type_id must be greater than or equal to 0")
    private List<IdNameDTO> targetPriceTypeId;

    public IdNameDTO getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }

    public void setSourcePriceTypeId(IdNameDTO sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public List<IdNameDTO> getTargetPriceTypeId() {
        return targetPriceTypeId;
    }

    public void setTargetPriceTypeId(List<IdNameDTO> targetPriceTypeId) {
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
