package es.onebox.internal.automaticsales.processsales.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SectorPriceZoneKey {

    private String sector;
    private String priceZone;

    public SectorPriceZoneKey() {
    }

    public SectorPriceZoneKey(String sector, String priceZone) {
        this.sector = sector;
        this.priceZone = priceZone;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(String priceZone) {
        this.priceZone = priceZone;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
