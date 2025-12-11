package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatPricesRecord;

import java.io.Serializable;

public class SeasonTicketChangeSeatPricesRecord extends CpanelSeasonTicketChangeSeatPricesRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sourcePriceTypeName;
    private String targetPriceTypeName;
    private String rateName;

    public String getSourcePriceTypeName() {
        return sourcePriceTypeName;
    }

    public void setSourcePriceTypeName(String sourcePriceTypeName) {
        this.sourcePriceTypeName = sourcePriceTypeName;
    }

    public String getTargetPriceTypeName() {
        return targetPriceTypeName;
    }

    public void setTargetPriceTypeName(String targetPriceTypeName) {
        this.targetPriceTypeName = targetPriceTypeName;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }
}
