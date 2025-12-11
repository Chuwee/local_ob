package es.onebox.mgmt.oneboxinvoicing.dto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class OneboxInvoiceEntitiesDTO extends ArrayList<OneboxInvoiceEntityDTO> {

    public OneboxInvoiceEntitiesDTO() {
    }

    public OneboxInvoiceEntitiesDTO(@NotNull Collection<? extends OneboxInvoiceEntityDTO> c) {
        super(c);
    }
}
