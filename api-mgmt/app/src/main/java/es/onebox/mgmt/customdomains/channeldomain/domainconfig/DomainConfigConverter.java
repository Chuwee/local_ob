package es.onebox.mgmt.customdomains.channeldomain.domainconfig;

import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.AllowedChannelsRuleType;
import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.DomainConfigDTO;
import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.DomainFallbackConfigDTO;
import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.DomainFallbackConfigRuleDTO;
import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.SocialLoginConfigDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.SocialLoginConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainFallbackConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainFallbackConfigRule;

import java.util.List;

public class DomainConfigConverter {

    private DomainConfigConverter() {
    }

    public static DomainConfigDTO fromMs(DomainConfig in) {
        if (in == null) return null;
        DomainConfigDTO out = new DomainConfigDTO();
        out.setSocialLogin(fromMs(in.getSocialLogin()));
        out.setDomainFallback(fromMs(in.getDomainFallback()));
        return out;
    }

    private static DomainFallbackConfigDTO fromMs(DomainFallbackConfig in) {
        if (in == null) return null;
        DomainFallbackConfigDTO out = new DomainFallbackConfigDTO();
        out.setEnabled(in.getEnabled());
        out.setChannelsAllowedMode(in.getChannelsAllowedMode());
        List<DomainFallbackConfigRuleDTO> rules = in.getRules() == null ? null : in.getRules().stream().map(DomainConfigConverter::fromMs).toList();
        out.setRules(rules);
        out.setDefaultRedirectionUrl(in.getDefaultRedirectionUrl());
        return out;
    }

    private static DomainFallbackConfigRuleDTO fromMs(DomainFallbackConfigRule in) {
        if (in == null) return null;
        DomainFallbackConfigRuleDTO out = new DomainFallbackConfigRuleDTO();
        out.setType(AllowedChannelsRuleType.valueOf(in.getType().name()));
        out.setValues(in.getValues());
        return out;
    }

    private static SocialLoginConfigDTO fromMs(SocialLoginConfig in) {
        if (in == null) return null;
        SocialLoginConfigDTO out = new SocialLoginConfigDTO();
        out.setGoogleClientId(in.getGoogleClientId());
        return out;
    }

    public static DomainConfig toMs(DomainConfigDTO in) {
        if (in == null) return null;
        DomainConfig out = new DomainConfig();
        out.setSocialLogin(toMs(in.getSocialLogin()));
        out.setDomainFallback(toMs(in.getDomainFallback()));
        return out;
    }

    private static DomainFallbackConfig toMs(DomainFallbackConfigDTO in) {
        if (in == null) return null;
        DomainFallbackConfig out = new DomainFallbackConfig();
        out.setEnabled(in.getEnabled());
        out.setChannelsAllowedMode(in.getChannelsAllowedMode());
        if (in.getRules() != null) {
            out.setRules(in.getRules().stream().map(DomainConfigConverter::toMs).toList());
        }
        out.setDefaultRedirectionUrl(in.getDefaultRedirectionUrl());
        return out;
    }

    private static DomainFallbackConfigRule toMs(DomainFallbackConfigRuleDTO in) {
        if (in == null) return null;
        DomainFallbackConfigRule out = new DomainFallbackConfigRule();
        out.setType(es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.AllowedChannelsRuleType.valueOf(in.getType().name()));
        out.setValues(in.getValues());
        return out;
    }

    private static SocialLoginConfig toMs(SocialLoginConfigDTO in) {
        if (in == null) return null;
        SocialLoginConfig out = new SocialLoginConfig();
        out.setGoogleClientId(in.getGoogleClientId());
        return out;
    }
}
