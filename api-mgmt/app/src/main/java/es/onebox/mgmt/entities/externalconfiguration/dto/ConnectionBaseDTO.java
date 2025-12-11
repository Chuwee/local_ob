package es.onebox.mgmt.entities.externalconfiguration.dto;

import java.io.Serial;
import java.io.Serializable;

public class ConnectionBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7177466770253282128L;

    private String url;
    private CredentialsBaseDTO credentials;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CredentialsBaseDTO getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsBaseDTO credentials) {
        this.credentials = credentials;
    }
}
