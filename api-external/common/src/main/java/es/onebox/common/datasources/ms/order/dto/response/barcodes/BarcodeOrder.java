package es.onebox.common.datasources.ms.order.dto.response.barcodes;

import java.io.Serial;
import java.io.Serializable;

public class BarcodeOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 7246926964029356729L;

    private String code;
    private String provider;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
