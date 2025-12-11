package es.onebox.mgmt.salerequests.taxes.dto;

import es.onebox.mgmt.salerequests.taxes.enums.SaleRequestSurchargesTaxesOriginDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRequestSurchargesTaxesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SaleRequestSurchargesTaxesOriginDTO origin;
    private List<TaxInfoDTO> taxes;

    public SaleRequestSurchargesTaxesOriginDTO getOrigin() {
        return origin;
    }

    public void setOrigin(SaleRequestSurchargesTaxesOriginDTO origin) {
        this.origin = origin;
    }

    public List<TaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfoDTO> taxes) {
        this.taxes = taxes;
    }
}
