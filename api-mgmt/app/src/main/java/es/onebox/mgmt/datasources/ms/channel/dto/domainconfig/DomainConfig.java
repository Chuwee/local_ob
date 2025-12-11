package es.onebox.mgmt.datasources.ms.channel.dto.domainconfig;

import java.io.Serial;
import java.io.Serializable;


public class DomainConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    private DomainFallbackConfig domainFallback;
    private SocialLoginConfig socialLogin;

    public DomainConfig() {
    }

    public DomainFallbackConfig getDomainFallback() {
        return domainFallback;
    }

    public void setDomainFallback(DomainFallbackConfig domainFallback) {
        this.domainFallback = domainFallback;
    }

    public SocialLoginConfig getSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(SocialLoginConfig socialLogin) {
        this.socialLogin = socialLogin;
    }
}
