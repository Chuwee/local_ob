package es.onebox.common.datasources.ms.client.dto.response;

import es.onebox.common.datasources.common.dto.Metadata;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;

import java.io.Serial;
import java.io.Serializable;

public class CustomersResponse extends BaseResponseCollection<CustomerResponse, Metadata>  implements Serializable {

    @Serial
    private static final long serialVersionUID = 6526143602735564832L;
}
