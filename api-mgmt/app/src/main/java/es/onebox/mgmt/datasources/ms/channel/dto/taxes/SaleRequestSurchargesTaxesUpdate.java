package es.onebox.mgmt.datasources.ms.channel.dto.taxes;

import es.onebox.mgmt.datasources.ms.channel.enums.SaleRequestSurchargesTaxesOrigin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRequestSurchargesTaxesUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SaleRequestSurchargesTaxesOrigin origin;
    private List<Tax> taxes;

    public SaleRequestSurchargesTaxesOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(SaleRequestSurchargesTaxesOrigin origin) {
        this.origin = origin;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }
}
