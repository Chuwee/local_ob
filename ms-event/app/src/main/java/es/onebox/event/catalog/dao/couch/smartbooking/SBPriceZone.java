package es.onebox.event.catalog.dao.couch.smartbooking;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public class SBPriceZone {

    private String name;
    private Integer idGroupZoneAvet;
    private String idGroupZoneFCB;
    private SBPriceZoneSaleMode saleMode;
    private Map<String, SBPriceList> priceListMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdGroupZoneAvet() {
        return idGroupZoneAvet;
    }

    public void setIdGroupZoneAvet(Integer idGroupZoneAvet) {
        this.idGroupZoneAvet = idGroupZoneAvet;
    }

    public String getIdGroupZoneFCB() {
        return idGroupZoneFCB;
    }

    public void setIdGroupZoneFCB(String idGroupZoneFCB) {
        this.idGroupZoneFCB = idGroupZoneFCB;
    }

    public SBPriceZoneSaleMode getSaleMode() {
        return saleMode;
    }

    public void setSaleMode(SBPriceZoneSaleMode saleMode) {
        this.saleMode = saleMode;
    }

    public Map<String, SBPriceList> getPriceListMap() {
        return priceListMap;
    }

    public void setPriceListMap(Map<String, SBPriceList> priceListMap) {
        this.priceListMap = priceListMap;
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
