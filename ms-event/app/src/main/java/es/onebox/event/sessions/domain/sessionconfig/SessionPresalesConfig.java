package es.onebox.event.sessions.domain.sessionconfig;


import java.io.Serial;
import java.io.Serializable;

public class SessionPresalesConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -8374928374628374928L;

    private PresalesRedirectionPolicy presalesRedirectionPolicy;

    public PresalesRedirectionPolicy getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicy presalesRedirectionPolicy) {this.presalesRedirectionPolicy = presalesRedirectionPolicy;}
}
