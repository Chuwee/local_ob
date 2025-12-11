package es.onebox.mgmt.customdomains.common.converter;

import es.onebox.mgmt.customdomains.common.dto.CustomDomainSetting;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsMode;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.customdomains.common.dto.CustomDomain;
import es.onebox.mgmt.customdomains.common.dto.DomainMode;

import java.util.stream.Collectors;

public class DomainSettingsConverter {

    private DomainSettingsConverter() {
    }

    public static DomainSettings fromDTO(DomainSettingsDTO domainSettingsDTO) {
        if (domainSettingsDTO == null) {
            return null;
        }

        return new DomainSettings(
                domainSettingsDTO.getEnabled(),
                DomainMode.valueOf(domainSettingsDTO.getMode().name()),
                domainSettingsDTO.getDomains().stream()
                        .map(DomainSettingsConverter::convertToCustomDomain)
                        .collect(Collectors.toList())
        );
    }

    public static DomainSettingsDTO toDTO(DomainSettings domainSettings) {
        if (domainSettings == null) {
            return null;
        }

        return new DomainSettingsDTO(
                domainSettings.getEnabled(),
                DomainSettingsMode.valueOf(domainSettings.getMode().name()),
                domainSettings.getDomains().stream()
                        .map(DomainSettingsConverter::convertToCustomDomainSetting)
                        .collect(Collectors.toList())
        );
    }

    private static CustomDomain convertToCustomDomain(CustomDomainSetting customDomainSetting) {
        return new CustomDomain(
                customDomainSetting.getDomain(),
                customDomainSetting.getDefaultDomain()
        );
    }

    private static CustomDomainSetting convertToCustomDomainSetting(CustomDomain customDomain) {
        return new CustomDomainSetting(
                customDomain.getDomain(),
                customDomain.getDefaultDomain()
        );
    }

}
