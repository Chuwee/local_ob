package es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public class DomainConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    @Valid
    @JsonProperty("domain_fallback")
    private DomainFallbackConfigDTO domainFallback;
    @Valid
    @JsonProperty("social_login")
    private SocialLoginConfigDTO socialLogin;

    public DomainConfigDTO() {
    }

    public DomainFallbackConfigDTO getDomainFallback() {
        return domainFallback;
    }

    public void setDomainFallback(DomainFallbackConfigDTO domainFallback) {
        this.domainFallback = domainFallback;
    }

    public SocialLoginConfigDTO getSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(SocialLoginConfigDTO socialLogin) {
        this.socialLogin = socialLogin;
    }
}
