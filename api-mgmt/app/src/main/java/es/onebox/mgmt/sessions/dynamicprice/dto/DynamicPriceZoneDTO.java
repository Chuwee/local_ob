package es.onebox.mgmt.sessions.dynamicprice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class DynamicPriceZoneDTO {

    @JsonProperty("id_price_zone")
    Long idPriceZone;

    @JsonProperty("price_zone_name")
    String priceZoneName;

    @JsonProperty("active_zone")
    private Long activeZone;

    private Boolean editable;

    @JsonProperty("available_capacity")
    private Long availableCapacity;

    @JsonProperty("capacity")
    private Long capacity;

    @JsonProperty("dynamic_prices")
    List<DynamicPriceDTO> dynamicPricesDTO;

    public Long getIdPriceZone() {
        return idPriceZone;
    }

    public void setIdPriceZone(Long idPriceZone) {
        this.idPriceZone = idPriceZone;
    }

    public String getPriceZoneName() {
        return priceZoneName;
    }

    public void setPriceZoneName(String priceZoneName) {
        this.priceZoneName = priceZoneName;
    }

    public Long getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(Long activeZone) {
        this.activeZone = activeZone;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Long getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Long availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public List<DynamicPriceDTO> getDynamicPricesDTO() {
        return dynamicPricesDTO;
    }

    public void setDynamicPricesDTO(List<DynamicPriceDTO> dynamicPricesDTO) {
        this.dynamicPricesDTO = dynamicPricesDTO;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
