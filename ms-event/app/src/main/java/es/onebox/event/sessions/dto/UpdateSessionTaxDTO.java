package es.onebox.event.sessions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSessionTaxDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "taxId is mandatory")
    @Min(value = 1, message = "taxId must be above 0")
    private Long taxId;

    @NotNull(message = "chargesTaxId is mandatory")
    @Min(value = 1, message = "chargesTaxId must be above 0")
    private Long chargesTaxId;


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

