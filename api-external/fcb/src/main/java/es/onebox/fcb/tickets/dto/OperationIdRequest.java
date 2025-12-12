package es.onebox.fcb.tickets.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class OperationIdRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4369917733428267054L;

    @NotNull
    private String code;
    private String orderType;
    private ZonedDateTime purchaseDate;
    private String timeZone;

    public OperationIdRequest() {
    }

    public OperationIdRequest(String code, String orderType, ZonedDateTime purchaseDate, String timeZone) {
        this.code = code;
        this.orderType = orderType;
        this.purchaseDate = purchaseDate;
        this.timeZone = timeZone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
