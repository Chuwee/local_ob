package es.onebox.mgmt.common.auth.converter;

import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.common.auth.dto.AuthenticatorDTO;
import es.onebox.mgmt.common.auth.dto.MaxMembersDTO;
import es.onebox.mgmt.common.auth.dto.PortalSettingsDTO;
import es.onebox.mgmt.common.auth.enums.AuthenticatorTypeDTO;
import es.onebox.mgmt.common.auth.enums.CustomerCreationDTO;
import es.onebox.mgmt.common.auth.enums.TriggerOnDTO;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.dto.AuthenticationMethod;
import es.onebox.mgmt.datasources.common.dto.Authenticator;
import es.onebox.mgmt.datasources.common.dto.MaxMembers;
import es.onebox.mgmt.datasources.common.dto.PortalSettings;
import es.onebox.mgmt.datasources.common.enums.AuthenticationType;
import es.onebox.mgmt.datasources.common.enums.AuthenticatorType;
import es.onebox.mgmt.datasources.common.enums.CustomerCreation;
import es.onebox.mgmt.datasources.common.enums.TriggerOn;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorChannelConfig;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.entities.dto.PhoneValidatorDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AuthConverter {

    private AuthConverter() {
    }

    public static AuthConfig toAuthConfig(AuthConfigDTO in) {
        AuthConfig out = new AuthConfig();
        if (in == null) {
            return out;
        }
        out.setEnabled(in.getEnabled());
        out.setUseEntityConfig(in.getUseEntityConfig());
        if (in.getMaxMembers() != null) {
            out.setMaxMembers(toMaxMembers(in.getMaxMembers()));
        }
        out.setAuthenticationMethods(AuthConverter.toAuthenticationMethods(in.getAuthenticators()));
        out.setSettings(toSettings(in.getSettings()));
        return out;
    }

    private static List<AuthenticationMethod> toAuthenticationMethods(List<AuthenticatorDTO> in) {
        if (in == null) {
            return null;
        }
        return in.stream()
                .collect(Collectors.groupingBy(AuthenticationType::fromSettings))
                .entrySet().stream()
                    .map(AuthConverter::toAuthenticationMethod)
                    .collect(Collectors.toList());
    }

    private static AuthenticationMethod toAuthenticationMethod(Map.Entry<AuthenticationType, List<AuthenticatorDTO>> in) {
        AuthenticationMethod out = new AuthenticationMethod();
        out.setType(in.getKey());
        out.setAuthenticators(toAuthenticators(in.getValue()));
        return out;
    }

    private static List<Authenticator> toAuthenticators(List<AuthenticatorDTO> in) {
        if (in == null) {
            return null;
        }
        return in.stream().map(AuthConverter::toAuthenticator).toList();
    }

    private static Authenticator toAuthenticator(AuthenticatorDTO in) {
        Authenticator out = new Authenticator();
        AuthenticatorType type = AuthenticatorType.fromDTO(in.getType());
        out.setType(type);
        out.setId(in.getId());
        out.setCustomerCreation(CustomerCreation.fromDTO(in.getCustomerCreation()));
        out.setProperties(in.getProperties());
        return out;
    }
    
    private static MaxMembers toMaxMembers(MaxMembersDTO in) {
        MaxMembers out = new MaxMembers();
        out.setEnabled(in.getEnabled());
        out.setLimit(in.getLimit());
        return out;
    }

    private static PortalSettings toSettings(PortalSettingsDTO in) {
        if(in == null){
            return null;
        }
        PortalSettings portalSettings = new PortalSettings();
        portalSettings.setMode(in.getMode());
        portalSettings.setAccountCreation(in.getAccountCreation());
        portalSettings.setUserDataEditable(in.getUserDataEditable());
        portalSettings.setBlockedCustomerTypesEnabled(in.getBlockedCustomerTypesEnabled());
        portalSettings.setBlockedCustomerTypes(in.getBlockedCustomerTypes());
        if(CollectionUtils.isNotEmpty(in.getTriggersOn())){
            portalSettings.setTriggersOn(in.getTriggersOn().stream()
                    .map(TriggerOn::fromtDTO)
                    .toList());
        }
        return portalSettings;
    }


    public static AuthConfigDTO toAuthConfigDTO(AuthConfig in, PhoneValidatorEntityConfig phoneValidatorEntityConfig) {
        AuthConfigDTO out = new AuthConfigDTO();
        if (in == null && phoneValidatorEntityConfig != null) {
            return out;
        }
        if (in != null) {
            out.setEnabled(BooleanUtils.isTrue(in.getEnabled()));
            out.setUseEntityConfig(BooleanUtils.isTrue(in.getUseEntityConfig()));
            if (in.getMaxMembers() != null) {
                out.setMaxMembers(toMaxMembersDTO(in.getMaxMembers()));
            }
            if (BooleanUtils.isNotTrue(in.getUseEntityConfig())){
                out.setAuthenticators(AuthConverter.toAuthenticatorsDTO(in.getAuthenticationMethods()));
                out.setSettings(AuthConverter.toSettingsDTO(in.getSettings()));
            }
        }
        if (phoneValidatorEntityConfig != null) {
            if (out.getSettings() == null) {
                out.setSettings(new PortalSettingsDTO());
            }
            out.getSettings().setPhoneValidator(new PhoneValidatorDTO());
            out.getSettings().getPhoneValidator().setEnabled(phoneValidatorEntityConfig.getEnabled());
            out.getSettings().getPhoneValidator().setValidatorId(phoneValidatorEntityConfig.getValidatorId());
            out.getSettings().getPhoneValidator().setValidatorIds(phoneValidatorEntityConfig.getValidatorIds());
        }
        return out;
    }

    public static AuthConfigDTO toAuthConfigDTO(AuthConfig in, PhoneValidatorChannelConfig phoneValidatorChannelConfig) {
        AuthConfigDTO out = new AuthConfigDTO();
        if (in == null && phoneValidatorChannelConfig != null) {
            return out;
        }
        if (in != null) {
            out.setEnabled(BooleanUtils.isTrue(in.getEnabled()));
            out.setUseEntityConfig(BooleanUtils.isTrue(in.getUseEntityConfig()));
            if (in.getMaxMembers() != null) {
                out.setMaxMembers(toMaxMembersDTO(in.getMaxMembers()));
            }
            if (BooleanUtils.isNotTrue(in.getUseEntityConfig())){
                out.setAuthenticators(AuthConverter.toAuthenticatorsDTO(in.getAuthenticationMethods()));
                out.setSettings(AuthConverter.toSettingsDTO(in.getSettings()));
            }
        }
        if (phoneValidatorChannelConfig != null) {
            if (out.getSettings() == null) {
                out.setSettings(new PortalSettingsDTO());
            }
            out.getSettings().setPhoneValidator(new PhoneValidatorDTO());
            out.getSettings().getPhoneValidator().setEnabled(phoneValidatorChannelConfig.getEnabled());
            out.getSettings().getPhoneValidator().setValidatorId(phoneValidatorChannelConfig.getValidatorId());
            out.getSettings().getPhoneValidator().setValidatorIds(phoneValidatorChannelConfig.getValidatorIds());
        }
        return out;
    }

    private static List<AuthenticatorDTO> toAuthenticatorsDTO(List<AuthenticationMethod> in) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }
         return in.stream().filter(Objects::nonNull).flatMap(authenticationMethod -> authenticationMethod.getAuthenticators().stream()
                         .filter(Objects::nonNull)
                         .map(AuthConverter::toAuthenticatorDTO))
                 .toList();
    }

    private static AuthenticatorDTO toAuthenticatorDTO(Authenticator in) {
        AuthenticatorDTO out = new AuthenticatorDTO();
        AuthenticatorTypeDTO type = AuthenticatorTypeDTO.toDTO(in.getType());
        out.setType(type);
        out.setId(in.getId());
        out.setCustomerCreation(CustomerCreationDTO.toDTO(in.getCustomerCreation()));
        out.setProperties(in.getProperties());
        return out;
    }

    private static MaxMembersDTO toMaxMembersDTO(MaxMembers in) {
        MaxMembersDTO out = new MaxMembersDTO();
        out.setEnabled(BooleanUtils.isTrue(in.getEnabled()));
        out.setLimit(in.getLimit());
        return out;
    }

    private static PortalSettingsDTO toSettingsDTO(PortalSettings in) {
        if (in == null) {
            return null;
        }
        PortalSettingsDTO out = new PortalSettingsDTO();
        out.setMode(in.getMode());
        if (CollectionUtils.isNotEmpty(in.getTriggersOn())) {
            out.setTriggersOn(in.getTriggersOn().stream()
                    .map(TriggerOnDTO::toDTO)
                    .toList());
        }
        out.setAccountCreation(in.getAccountCreation());
        out.setUserDataEditable(in.getUserDataEditable());
        out.setBlockedCustomerTypes(in.getBlockedCustomerTypes());
        out.setBlockedCustomerTypesEnabled(in.getBlockedCustomerTypesEnabled());

        return out;
    }

}
