package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PriceTypeRequestDTO extends CreateVenueTagConfigRequestDTO {

    private static final long serialVersionUID = 1L;

    private Long priority;

    @JsonProperty("additional_config")
    private PriceTypeAdditionalConfigDTO priceTypeAdditionalConfigDTO;

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

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
