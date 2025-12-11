package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.users.dto.MFA;

import java.io.Serial;
import java.io.Serializable;

public class MFARequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private MFA mfa;
    private String operator;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MFA getMfa() {
        return mfa;
    }

    public void setMfa(MFA mfa) {
        this.mfa = mfa;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
