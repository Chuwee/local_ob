package es.onebox.event.catalog.elasticsearch.dto.session;


import java.io.Serial;
import java.io.Serializable;

public class PresalesSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = -8374928374628374928L;

    private PresalesRedirectionPolicy redirectPolicy;

    public PresalesRedirectionPolicy getRedirectPolicy() {
        return redirectPolicy;
    }

    public void setRedirectPolicy(
        PresalesRedirectionPolicy redirectPolicy) {
        this.redirectPolicy = redirectPolicy;
    }
}
