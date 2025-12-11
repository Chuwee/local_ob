package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class ProducerInvoicePrefix extends BaseResponseCollection<InvoicePrefix, Metadata> {

    private static final long serialVersionUID = 1L;

    public ProducerInvoicePrefix() {
    }

    public ProducerInvoicePrefix(List<InvoicePrefix> response, Metadata metadata) {
        super(response, metadata);
    }

}
