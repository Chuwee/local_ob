package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class ExternalBarcodeEntityConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String externalBarcodeFormatId;
    private Boolean allowExternalBarcode;

    public String getExternalBarcodeFormatId() {
        return externalBarcodeFormatId;
    }

    public void setExternalBarcodeFormatId(String externalBarcodeFormatId) {
        this.externalBarcodeFormatId = externalBarcodeFormatId;
    }

    public Boolean getAllowExternalBarcode() {
        return allowExternalBarcode;
    }

    public void setAllowExternalBarcode(Boolean allowExternalBarcode) {
        this.allowExternalBarcode = allowExternalBarcode;
    }

}
