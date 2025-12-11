package es.onebox.event.sessions.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DynamicPriceZoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean isActive;
    private Long idPriceZone;
    private Long activeZone;
    private List<DynamicPriceDTO> dynamicPricesDTO;
    private DynamicPriceDTO defaultPriceDTO;

    public Long getIdPriceZone() {
        return idPriceZone;
    }

    public void setIdPriceZone(Long idPriceZone) {
        this.idPriceZone = idPriceZone;
    }

    public Long getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(Long activeZone) {
        this.activeZone = activeZone;
    }

    public List<DynamicPriceDTO> getDynamicPricesDTO() {
        return dynamicPricesDTO;
    }

    public void setDynamicPricesDTO(List<DynamicPriceDTO> dynamicPricesDTO) {
        this.dynamicPricesDTO = dynamicPricesDTO;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public DynamicPriceDTO getDefaultPriceDTO() {
        return defaultPriceDTO;
    }

    public void setDefaultPriceDTO(DynamicPriceDTO defaultPriceDTO) {
        this.defaultPriceDTO = defaultPriceDTO;
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
