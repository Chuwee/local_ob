package es.onebox.mgmt.datasources.ms.channel.dto.domainconfig;

import java.io.Serial;
import java.io.Serializable;


public class SocialLoginConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    private String googleClientId;

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }
}
