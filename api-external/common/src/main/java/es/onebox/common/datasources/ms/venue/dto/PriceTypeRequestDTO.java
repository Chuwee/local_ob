package es.onebox.common.datasources.ms.venue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class PriceTypeRequestDTO extends VenueTagConfigRequestDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("additional_config")
    private PriceTypeAdditionalConfigDTO priceTypeAdditionalConfigDTO;

    public PriceTypeAdditionalConfigDTO getPriceTypeAdditionalConfigDTO() {
        return priceTypeAdditionalConfigDTO;
    }

    public void setPriceTypeAdditionalConfigDTO(PriceTypeAdditionalConfigDTO priceTypeAdditionalConfigDTO) {
        this.priceTypeAdditionalConfigDTO = priceTypeAdditionalConfigDTO;
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
