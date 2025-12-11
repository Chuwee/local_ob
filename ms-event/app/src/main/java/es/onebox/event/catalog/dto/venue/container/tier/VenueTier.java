package es.onebox.event.catalog.dto.venue.container.tier;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class VenueTier implements Serializable {

    @Serial
    private static final long serialVersionUID = -8999721702682935396L;

    private Long id;
    private String name;
    private Boolean active;
    private String condition;
    private ZonedDateTime startDate;
    private String olsonId;
    private Long priceTypeId;
    private Double price;
    private Long maxCapacity;
    private List<VenueTierCommElement> commElements;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<VenueTierCommElement> getCommElements() {
        return commElements;
    }

    public void setCommElements(List<VenueTierCommElement> commElements) {
        this.commElements = commElements;
    }
}
