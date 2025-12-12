package es.onebox.ms.notification.webhooks.dto;

import java.io.Serial;
import java.io.Serializable;

public class NotificationMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    private String confId;
    private String event;
    private String action;
    private String notificationSubtype;
    private String code;
    private PayloadRequest payload;
    private String signature;
    private String prevCode;
    private PayloadRequest prevPayload;
    private String prevSignature;
    private Boolean reimbursement;
    private String printStatus;
    private Long itemId;


    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNotificationSubtype() {
        return notificationSubtype;
    }

    public void setNotificationSubtype(String notificationSubtype) {
        this.notificationSubtype = notificationSubtype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PayloadRequest getPayload() {
        return payload;
    }

    public void setPayload(PayloadRequest payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPrevCode() {
        return prevCode;
    }

    public void setPrevCode(String prevCode) {
        this.prevCode = prevCode;
    }

    public PayloadRequest getPrevPayload() {
        return prevPayload;
    }

    public void setPrevPayload(PayloadRequest prevPayload) {
        this.prevPayload = prevPayload;
    }

    public String getPrevSignature() {
        return prevSignature;
    }

    public void setPrevSignature(String prevSignature) {
        this.prevSignature = prevSignature;
    }

    public Boolean getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(Boolean reimbursement) {
        this.reimbursement = reimbursement;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
