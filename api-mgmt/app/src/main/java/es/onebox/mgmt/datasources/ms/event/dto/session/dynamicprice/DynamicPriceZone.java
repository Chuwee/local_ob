package es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DynamicPriceZone implements Serializable {

    @Serial
    private static final long serialVersionUID = 5069061499909023343L;

    private Long idPriceZone;
    private String priceZoneName;
    private Long activeZone;
    private Boolean editable;
    private List<DynamicPrice> dynamicPricesDTO;

    public Long getIdPriceZone() {
        return idPriceZone;
    }

    public void setIdPriceZone(Long idPriceZone) {
        this.idPriceZone = idPriceZone;
    }

    public String getPriceZoneName() {
        return priceZoneName;
    }

    public Long getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(Long activeZone) {
        this.activeZone = activeZone;
    }

    public void setPriceZoneName(String priceZoneName) {
        this.priceZoneName = priceZoneName;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public List<DynamicPrice> getDynamicPricesDTO() {
        return dynamicPricesDTO;
    }

    public void setDynamicPricesDTO(List<DynamicPrice> dynamicPricesDTO) {
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
