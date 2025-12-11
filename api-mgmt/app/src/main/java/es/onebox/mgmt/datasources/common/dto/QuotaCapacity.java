package es.onebox.mgmt.datasources.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class QuotaCapacity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7684098920767330918L;

    private Long id;
    private String name;
    private Boolean onSale;
    private Long maxCapacity;
    private List<IdCapacity> priceTypes;

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

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public Long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<IdCapacity> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<IdCapacity> priceTypes) {
        this.priceTypes = priceTypes;
    }
}
