package es.onebox.event.products.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSessionDeliveryPointDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ProductSessionDateDTO dates;
    private List<ProductSessionDeliveryPointDetailDTO> deliveryPoints;
    private Boolean isSmartBooking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductSessionDateDTO getDates() {
        return dates;
    }

    public void setDates(ProductSessionDateDTO dates) {
        this.dates = dates;
    }

    public List<ProductSessionDeliveryPointDetailDTO> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<ProductSessionDeliveryPointDetailDTO> deliveryPoints) {
        this.deliveryPoints = deliveryPoints;
    }

    public Boolean getSmartBooking() {
        return isSmartBooking;
    }

    public void setSmartBooking(Boolean smartBooking) {
        isSmartBooking = smartBooking;
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
