package es.onebox.mgmt.oneboxinvoicing.dto;

import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record CreateOneboxInvoiceEntityRequestDTO(@NotNull @Min(0) Double fixed,
                                                  @NotNull @Min(0) Double variable,
                                                  @NotNull @Min(0) Double min,
                                                  @NotNull @Min(0) Double max,
                                                  @NotNull @Min(0) Double invitation,
                                                  @NotNull @Min(0) Double refund,
                                                  @NotNull OneboxInvoiceType type
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
