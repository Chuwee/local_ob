package es.onebox.chelsea.dto;

import java.io.Serial;
import java.io.Serializable;

public class NotificationMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    private String confId;
    private String event;
    private String action;
    private String code;
    private String movementId;
    private String signature;
    private String prevCode;
    private String prevSignature;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getPrevSignature() {
        return prevSignature;
    }

    public void setPrevSignature(String prevSignature) {
        this.prevSignature = prevSignature;
    }

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

}
