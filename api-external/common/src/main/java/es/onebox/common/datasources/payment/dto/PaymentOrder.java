package es.onebox.common.datasources.payment.dto;

import es.onebox.common.datasources.payment.dto.enums.PaymentStatus;

import java.io.Serializable;

public class PaymentOrder implements Serializable {

    private static final long serialVersionUID = -5310645166021730467L;

    private String locator;
    private PaymentStatus status;
    private String gatewaySid;
    private String paymentMethod;
    private String acquirer;

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }
}
