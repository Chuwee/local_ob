package es.onebox.mgmt.datasources.ms.entity.dto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class OneboxInvoiceEntitiesFilter extends ArrayList<OneboxInvoiceEntityFilter> {

    private static final long serialVersionUID = 2L;

    public OneboxInvoiceEntitiesFilter() {
    }

    public OneboxInvoiceEntitiesFilter(@NotNull Collection<? extends OneboxInvoiceEntityFilter> c) {
        super(c);
    }
}
