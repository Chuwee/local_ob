package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelTaxes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ChannelTaxInfo> surcharges;

    public List<ChannelTaxInfo> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<ChannelTaxInfo> surcharges) {
        this.surcharges = surcharges;
    }
}
