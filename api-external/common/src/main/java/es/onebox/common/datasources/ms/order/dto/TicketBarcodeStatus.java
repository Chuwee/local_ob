package es.onebox.common.datasources.ms.order.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class TicketBarcodeStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 3105529463339843698L;

    private Integer sessionId;
    private Integer variantId;
    private String barcode;
    private ZonedDateTime sessionStartDate;
    private String status;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ZonedDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(ZonedDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
