package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PackItemSessionDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    private PackEventDTO event;

    private PackSessionDateDTO dates;

    @JsonProperty("venue_template")
    private PackItemVenueTemplateDTO venueTemplate;

    @JsonProperty("price_type")
    private IdNameDTO priceType;

    @JsonProperty("price_type_mapping")
    private List<PackItemPriceTypeMappingDTO> priceTypeMapping;

    public PackEventDTO getEvent() {
        return event;
    }

    public void setEvent(PackEventDTO event) {
        this.event = event;
    }

    public PackSessionDateDTO getDates() {
        return dates;
    }

    public void setDates(PackSessionDateDTO dates) {
        this.dates = dates;
    }

    public PackItemVenueTemplateDTO getVenueTemplate() {
        return venueTemplate;
    }

    public void setVenueTemplate(PackItemVenueTemplateDTO venueTemplate) {
        this.venueTemplate = venueTemplate;
    }

    public IdNameDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(IdNameDTO priceType) {
        this.priceType = priceType;
    }

    public List<PackItemPriceTypeMappingDTO> getPriceTypeMapping() {
        return priceTypeMapping;
    }

    public void setPriceTypeMapping(List<PackItemPriceTypeMappingDTO> priceTypeMapping) {
        this.priceTypeMapping = priceTypeMapping;
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
