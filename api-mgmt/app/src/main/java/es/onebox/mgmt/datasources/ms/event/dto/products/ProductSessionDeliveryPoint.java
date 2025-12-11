package es.onebox.mgmt.datasources.ms.event.dto.products;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductSessionDeliveryPoint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ProductSessionDate dates;
    private List<ProductSessionDeliveryPointDetail> deliveryPoints;
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

    public ProductSessionDate getDates() {
        return dates;
    }

    public void setDates(ProductSessionDate dates) {
        this.dates = dates;
    }

    public List<ProductSessionDeliveryPointDetail> getDeliveryPoints() {
        return deliveryPoints;
    }

    public void setDeliveryPoints(List<ProductSessionDeliveryPointDetail> deliveryPoints) {
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
