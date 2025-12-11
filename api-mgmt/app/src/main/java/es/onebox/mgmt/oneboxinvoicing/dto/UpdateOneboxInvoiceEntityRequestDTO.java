package es.onebox.mgmt.oneboxinvoicing.dto;

import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;

public record UpdateOneboxInvoiceEntityRequestDTO(@Min(0) Double fixed, @Min(0) Double variable, @Min(0) Double min,
                                                  @Min(0) Double max, @Min(0) Double invitation, @Min(0) Double refund) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
