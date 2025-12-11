package es.onebox.mgmt.integrations.barcodes;

import java.io.Serializable;

public class ExternalBarcodeConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    public ExternalBarcodeConfigDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
