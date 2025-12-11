package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketTax implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long taxId;
    private Long chargesTaxId;

    public SeasonTicketTax() {
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public Long getChargesTaxId() {
        return chargesTaxId;
    }

    public void setChargesTaxId(Long chargesTaxId) {
        this.chargesTaxId = chargesTaxId;
    }
}
