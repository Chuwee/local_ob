package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelTierCupoRecord;

public class TierSaleGroupRecord extends CpanelTierCupoRecord {

    private Integer priceTypeId;
    private Integer available;
    private Integer tierLimit;

    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getTierLimit() {
        return tierLimit;
    }

    public void setTierLimit(Integer tierLimit) {
        this.tierLimit = tierLimit;
    }
}
