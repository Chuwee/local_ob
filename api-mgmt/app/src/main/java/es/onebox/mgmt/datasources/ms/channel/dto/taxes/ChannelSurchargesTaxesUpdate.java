package es.onebox.mgmt.datasources.ms.channel.dto.taxes;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSurchargesTaxesOrigin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSurchargesTaxesUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelSurchargesTaxesOrigin origin;
    private List<Tax> taxes;

    public ChannelSurchargesTaxesOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(ChannelSurchargesTaxesOrigin origin) {
        this.origin = origin;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }
}
