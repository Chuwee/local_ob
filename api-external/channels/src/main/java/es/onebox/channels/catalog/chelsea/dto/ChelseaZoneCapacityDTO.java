package es.onebox.channels.catalog.chelsea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.channels.catalog.generic.dto.SessionPriceDTO;
import es.onebox.common.datasources.catalog.dto.ChannelEventCategory;
import es.onebox.common.datasources.catalog.dto.ChannelEventImages;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ChelseaZoneCapacityDTO implements Serializable {

    private static final long serialVersionUID = 6404355482133225518L;

    private IdNameCodeDTO sector;
    @JsonProperty("price_type")
    private IdNameDTO priceType;
    private SessionPriceDTO price;
    private Map<String, String> url;
    private ChelseaZoneAvailabilityDTO availability;

    public IdNameCodeDTO getSector() {
        return sector;
    }

    public void setSector(IdNameCodeDTO sector) {
        this.sector = sector;
    }

    public IdNameDTO getPriceType() {
        return priceType;
    }

    public void setPriceType(IdNameDTO priceType) {
        this.priceType = priceType;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public SessionPriceDTO getPrice() {
        return price;
    }

    public void setPrice(SessionPriceDTO price) {
        this.price = price;
    }

    public ChelseaZoneAvailabilityDTO getAvailability() {
        return availability;
    }

    public void setAvailability(ChelseaZoneAvailabilityDTO availability) {
        this.availability = availability;
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
