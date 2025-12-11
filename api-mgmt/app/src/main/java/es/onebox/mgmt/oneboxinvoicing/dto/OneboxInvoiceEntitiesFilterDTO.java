package es.onebox.mgmt.oneboxinvoicing.dto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class OneboxInvoiceEntitiesFilterDTO extends ArrayList<OneboxInvoiceEntityFilterDTO> {

    private static final long serialVersionUID = 2L;

    public OneboxInvoiceEntitiesFilterDTO() {
    }

    public OneboxInvoiceEntitiesFilterDTO(@NotNull Collection<? extends OneboxInvoiceEntityFilterDTO> c) {
        super(c);
    }
}
