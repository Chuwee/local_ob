package es.onebox.mgmt.channels.taxes.dto;

import es.onebox.mgmt.channels.taxes.enums.ChannelSurchargesTaxesOriginDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSurchargesTaxesUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelSurchargesTaxesOriginDTO origin;
    private List<TaxDTO> taxes;

    public ChannelSurchargesTaxesOriginDTO getOrigin() {
        return origin;
    }

    public void setOrigin(ChannelSurchargesTaxesOriginDTO origin) {
        this.origin = origin;
    }

    public List<TaxDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxDTO> taxes) {
        this.taxes = taxes;
    }
}
