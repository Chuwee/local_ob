package es.onebox.event.catalog.dao.couch.smartbooking;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;
import java.util.Set;

public class SBPriceList {

    private Double unitPrice;
    private Double initialPrice;
    private Map<Integer, Set<String>> collectiveTypesByOrderTypeMap;

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(Double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public Map<Integer, Set<String>> getCollectiveTypesByOrderTypeMap() {
        return collectiveTypesByOrderTypeMap;
    }

    public void setCollectiveTypesByOrderTypeMap(Map<Integer, Set<String>> collectiveTypesByOrderTypeMap) {
        this.collectiveTypesByOrderTypeMap = collectiveTypesByOrderTypeMap;
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
