package es.onebox.mgmt.seasontickets.dto.tax;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class SeasonTicketTaxDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "tax_id is mandatory")
    @Min(value = 1, message = "tax_id must be above 0")
    @JsonProperty("tax_id")
    private Long taxId;

    @NotNull(message = "charges_tax_id is mandatory")
    @Min(value = 1, message = "charges_tax_id must be above 0")
    @JsonProperty("charges_tax_id")
    private Long chargesTaxId;

    public SeasonTicketTaxDTO() {
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
