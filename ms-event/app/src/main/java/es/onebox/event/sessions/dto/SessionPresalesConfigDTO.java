package es.onebox.event.sessions.dto;

import java.io.Serial;
import java.io.Serializable;

public class SessionPresalesConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5829475837458392847L;

    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicyDTO(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {this.presalesRedirectionPolicy = presalesRedirectionPolicy;}
}
