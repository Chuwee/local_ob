package es.onebox.mgmt.channels.taxes.dto;

import es.onebox.mgmt.channels.taxes.enums.ChannelSurchargesTaxesOriginDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSurchargesTaxesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelSurchargesTaxesOriginDTO origin;
    private List<TaxInfoDTO> taxes;

    public ChannelSurchargesTaxesOriginDTO getOrigin() {
        return origin;
    }

    public void setOrigin(ChannelSurchargesTaxesOriginDTO origin) {
        this.origin = origin;
    }

    public List<TaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfoDTO> taxes) {
        this.taxes = taxes;
    }
}
