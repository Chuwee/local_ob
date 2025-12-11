package es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;


public class SocialLoginConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    @JsonProperty("google_client_id")
    private String googleClientId;

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }
}
