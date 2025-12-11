package es.onebox.mgmt.salerequests.taxes.dto;

import es.onebox.mgmt.salerequests.taxes.enums.SaleRequestSurchargesTaxesOriginDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRequestsSurchargesTaxesUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SaleRequestSurchargesTaxesOriginDTO origin;
    private List<TaxDTO> taxes;

    public SaleRequestSurchargesTaxesOriginDTO getOrigin() {
        return origin;
    }

    public void setOrigin(SaleRequestSurchargesTaxesOriginDTO origin) {
        this.origin = origin;
    }

    public List<TaxDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxDTO> taxes) {
        this.taxes = taxes;
    }
}
