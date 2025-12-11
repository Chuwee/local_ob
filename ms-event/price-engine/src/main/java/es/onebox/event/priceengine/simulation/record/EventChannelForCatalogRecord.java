package es.onebox.event.priceengine.simulation.record;

import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class EventChannelForCatalogRecord extends CpanelEventoCanalRecord {

    protected List<ChannelTaxInfo> surchargesTaxes;

    public List<ChannelTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ChannelTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
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
