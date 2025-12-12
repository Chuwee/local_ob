package es.onebox.common.datasources.ms.client.dto.request;

import java.io.Serial;
import java.io.Serializable;

public class CreateCustomerResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 963566023055054650L;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
