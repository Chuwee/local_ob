package es.onebox.common.datasources.ms.order.dto.response.barcodes;

import es.onebox.core.serializer.dto.response.ListWithMetadata;

import java.io.Serial;
import java.io.Serializable;

public class ProductBarcodesResponse extends ListWithMetadata<ProductBarcode> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2869081359008941902L;
}
