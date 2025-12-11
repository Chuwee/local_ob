package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.users.enums.MFAState;

import java.io.Serial;
import java.io.Serializable;

public class MFAResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private MFAState state;
    private String message;

    public MFAState getState() {
        return state;
    }

    public void setState(MFAState state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}