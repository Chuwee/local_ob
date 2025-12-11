package es.onebox.mgmt.datasources.ms.entity.dto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class OneboxInvoiceEntities extends ArrayList<OneboxInvoiceEntity> {

    public OneboxInvoiceEntities() {
    }

    public OneboxInvoiceEntities(@NotNull Collection<? extends OneboxInvoiceEntity> c) {
        super(c);
    }
}
