package es.onebox.mgmt.entities.externalconfiguration.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.EnvironmentUtils;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.enums.ConnectionType;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.ApimConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ClubConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.SGAConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.SmartBookingConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.externalconfiguration.converter.ClubConfigConverter;
import es.onebox.mgmt.entities.externalconfiguration.converter.LinkClubConfigConverter;
import es.onebox.mgmt.entities.externalconfiguration.converter.StatusConverter;
import es.onebox.mgmt.entities.externalconfiguration.dto.ConnectionBaseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.CredentialsBaseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigRequestDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.InventoryProviderConfigResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.LinkClubConfigDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.SGAConfigDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.SgaConnectionDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.SmartBookingDTO;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetConnectionType;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment;
import es.onebox.mgmt.entities.externalconfiguration.enums.Status;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.entities.externalconfiguration.enums.AvetConnectionType.APIM;


@Service
public class ExternalEntityConfigurationService {

    private static final int FIXED_DELAY_THRESHOLD_MS = 1800000;

    private final SecurityManager securityManager;
    private final AvetConfigRepository avetConfigRepository;
    private final ConfigurableEnvironment env;
    private final EntitiesRepository entitiesRepository;
    private final DispatcherRepository dispatcherRepository;

    @Autowired
    public ExternalEntityConfigurationService(SecurityManager securityManager, AvetConfigRepository avetConfigRepository,
                                              ConfigurableEnvironment env, EntitiesRepository entitiesRepository, DispatcherRepository dispatcherRepository) {
        this.securityManager = securityManager;
        this.avetConfigRepository = avetConfigRepository;
        this.env = env;
        this.entitiesRepository = entitiesRepository;
        this.dispatcherRepository = dispatcherRepository;
    }

    public ExternalConfigResponseDTO getExternalEntityConfiguration(Long entityId) {
        Entity entity = validateAndGetEntityAndCheckAvet(entityId);
        ClubConfig clubConfig = avetConfigRepository.getClubConfigByEntity(entityId);
        ExternalConfigResponseDTO externalConfigResponse = ClubConfigConverter.toDto(clubConfig);
        externalConfigResponse.setEntityId(entity.getId());
        ExternalConfig externalConfig = entitiesRepository.getExternalConfig(entity.getId());
        fillExternalConfig(externalConfig, externalConfigResponse);
        fillConnectionStatus(externalConfigResponse);
        fillConnectionType(externalConfig, clubConfig, externalConfigResponse);
        return externalConfigResponse;
    }

    private void fillConnectionType(ExternalConfig externalConfig, ClubConfig clubConfig, ExternalConfigResponseDTO externalConfigResponse) {
        Boolean isApimEnabled = externalConfig == null || externalConfig.getApim() == null ? null: externalConfig.getApim().getEnabled();
        Boolean isSocketEnabled = clubConfig == null ? null: clubConfig.getConnectionBySocket();
        if(BooleanUtils.isTrue(isSocketEnabled)) {
            externalConfigResponse.setAvetConnectionType(AvetConnectionType.SOCKET);
        } else if (BooleanUtils.isTrue(isApimEnabled)) {
            externalConfigResponse.setAvetConnectionType(APIM);
        } else {
            externalConfigResponse.setAvetConnectionType(AvetConnectionType.WEBSERVICES);
        }
    }

    public Boolean isMembersEnabled(Long entityId) {
        ClubConfig clubConfig = avetConfigRepository.getClubConfigByEntity(entityId);
        return clubConfig != null ? clubConfig.getMembersEnabled() : Boolean.FALSE;
    }

    private void fillConnectionStatus(ExternalConfigResponseDTO externalConfigResponse) {
        if(BooleanUtils.isTrue(externalConfigResponse.getClubConfigMembersResponseDTO().getMembersEnabled())) {
            Status membersStatus = StatusConverter.toDto(dispatcherRepository.getConnectionStatus(externalConfigResponse.getEntityId(), ConnectionType.MEMBERS).getStatus());
            externalConfigResponse.getClubConfigMembersResponseDTO().getClubConfigMembersConnectionResponseDTO().setStatus(membersStatus);
        } else {
            externalConfigResponse.getClubConfigMembersResponseDTO().getClubConfigMembersConnectionResponseDTO().setStatus(Status.DISABLED);
        }
        Status ticketingStatus = StatusConverter.toDto(dispatcherRepository.getConnectionStatus(externalConfigResponse.getEntityId(), ConnectionType.TICKETING).getStatus());
        externalConfigResponse.getClubConfigTicketingResponseDTO().getClubConfigConnectionResponseDTO().setStatus(ticketingStatus);
    }


    private void fillExternalConfig(ExternalConfig externalConfig, ExternalConfigResponseDTO externalConfigResponseDTO) {
        if (externalConfig != null) {
            externalConfigResponseDTO.setSmartBookingDTO(convertToSmartBookingDTO(externalConfig.getSmartBooking()));
            externalConfigResponseDTO.setSgaConfigDTO(convertToSGAConfigDTO(externalConfig.getSga()));
            externalConfigResponseDTO.setInventoryProviders(externalConfig.getInventoryProviders());
            externalConfigResponseDTO.setSectorsValidation(externalConfig.getSectorsValidation());
        }
    }

    private SmartBookingDTO convertToSmartBookingDTO(SmartBookingConfig smartBookingConfig) {
        SmartBookingDTO smartBookingDTO = null;
        if (smartBookingConfig != null) {
            smartBookingDTO = new SmartBookingDTO();
            smartBookingDTO.setEnabled(smartBookingConfig.getEnabled());
            ConnectionBaseDTO connection = new ConnectionBaseDTO();
            connection.setUrl(smartBookingConfig.getUrl());
            CredentialsBaseDTO credentials = new CredentialsBaseDTO();
            credentials.setPassword(smartBookingConfig.getPassword());
            credentials.setUsername(smartBookingConfig.getUsername());
            connection.setCredentials(credentials);
            smartBookingDTO.setConnectionBaseDTO(connection);
        }
        return smartBookingDTO;
    }

    private SGAConfigDTO convertToSGAConfigDTO(SGAConfig sgaConfig) {
        if (sgaConfig == null) {
            return null;
        }
        SGAConfigDTO sgaConfigDTO = new SGAConfigDTO();
        sgaConfigDTO.setEnabled(sgaConfig.getEnabled());
        SgaConnectionDTO connection = new SgaConnectionDTO();
        connection.setUrl(sgaConfig.getUrl());
        connection.setAuthUrl(sgaConfig.getAuthUrl());
        connection.setClientId(sgaConfig.getClientId());
        connection.setScope(sgaConfig.getScope());
        connection.setProfile(sgaConfig.getProfile());
        connection.setSalesChannelId(sgaConfig.getSalesChannelId());
        CredentialsBaseDTO credentials = new CredentialsBaseDTO();
        credentials.setPassword(sgaConfig.getPassword());
        credentials.setUsername(sgaConfig.getUsername());
        connection.setCredentials(credentials);
        sgaConfigDTO.setSgaConnectionDTO(connection);
        return sgaConfigDTO;
    }

    public void updateExternalEntityConfiguration(Long entityId, ExternalConfigRequestDTO externalConfigRequestDTO) {
        validateAndGetEntity(entityId, externalConfigRequestDTO.getAvetWSEnvironment());
        AvetConnectionType connectionType = externalConfigRequestDTO.getConnectionType();

        if (externalConfigRequestDTO.getSgaConfig() == null) {
            validateExternalEntityRequest(externalConfigRequestDTO);
            ClubConfig clubConfig = ClubConfigConverter.toMs(externalConfigRequestDTO);
            setSocketConnection(clubConfig, connectionType);
            avetConfigRepository.updateClubConfigByEntity(entityId, clubConfig);
        }
        if (externalConfigRequestDTO.getSmartBooking() != null) {
            ExternalConfig externalConfig = new ExternalConfig();
            externalConfig.setSmartBooking(convertToSmartBooking(externalConfigRequestDTO.getSmartBooking()));
            externalConfig.setId(entityId);
            entitiesRepository.updateExternalConfig(entityId, externalConfig);
        }
        if (externalConfigRequestDTO.getSgaConfig() != null) {
            ExternalConfig externalConfig = new ExternalConfig();
            externalConfig.setSga(convertToSga(externalConfigRequestDTO.getSgaConfig()));
            externalConfig.setId(entityId);
            entitiesRepository.updateExternalConfig(entityId, externalConfig);
        }
        if (externalConfigRequestDTO.getSectorsValidation() != null) {
            ExternalConfig externalConfig = new ExternalConfig();
            externalConfig.setSectorsValidation(externalConfigRequestDTO.getSectorsValidation());
            externalConfig.setId(entityId);
            entitiesRepository.updateExternalConfig(entityId, externalConfig);
        }
        if (connectionType != null) {
            ExternalConfig externalConfig = new ExternalConfig();
            if(APIM.equals(connectionType)) {
                externalConfig.setApim(convertToApim(true));
            } else {
                externalConfig.setApim(convertToApim(false));
            }
            externalConfig.setId(entityId);
            entitiesRepository.updateExternalConfig(entityId, externalConfig);
        }
    }

    private void validateExternalEntityRequest(ExternalConfigRequestDTO request) {
        if (request.getClubConfigTicketingRequestDTO() != null &&
                request.getClubConfigTicketingRequestDTO().getClubConfigOperativeDTO() != null &&
                request.getClubConfigTicketingRequestDTO().getClubConfigOperativeDTO().getFixedDelayMs() != null &&
                request.getClubConfigTicketingRequestDTO().getClubConfigOperativeDTO().getFixedDelayMs() < FIXED_DELAY_THRESHOLD_MS) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "fixed-delay exceeds minimum value of 30m", null);
        }
    }

    private static void setSocketConnection(ClubConfig clubConfig, AvetConnectionType connectionType) {
        if(clubConfig != null && connectionType != null) {
            if(AvetConnectionType.SOCKET.equals(connectionType)) {
                clubConfig.setConnectionBySocket(true);
            } else {
                clubConfig.setConnectionBySocket(false);
            }
        }
    }

    private SmartBookingConfig convertToSmartBooking(SmartBookingDTO smartBookingDTO) {
        SmartBookingConfig smartBooking = null;
        if (smartBookingDTO != null) {
            smartBooking = new SmartBookingConfig();
            smartBooking.setEnabled(smartBookingDTO.getEnabled());
            if (smartBookingDTO.getConnectionBaseDTO() != null) {
                smartBooking.setUrl(smartBookingDTO.getConnectionBaseDTO().getUrl());
                if (smartBookingDTO.getConnectionBaseDTO().getCredentials() != null) {
                    smartBooking.setUsername(smartBookingDTO.getConnectionBaseDTO().getCredentials().getUsername());
                    smartBooking.setPassword(smartBookingDTO.getConnectionBaseDTO().getCredentials().getPassword());
                }
            }
        }
        return smartBooking;
    }

    private SGAConfig convertToSga(SGAConfigDTO sgaConfigDTO) {
        if (sgaConfigDTO == null) {
            return null;
        }
        SGAConfig sgaConfig = new SGAConfig();
        sgaConfig.setEnabled(sgaConfigDTO.getEnabled());
        decorateSgaConfigWithConnection(sgaConfigDTO.getSgaConnectionDTO(), sgaConfig);
        return sgaConfig;
    }

    private ApimConfig convertToApim(Boolean enabled) {
        ApimConfig apimConfig = new ApimConfig();
        apimConfig.setEnabled(enabled);
        return apimConfig;
    }

    private void decorateSgaConfigWithConnection(SgaConnectionDTO sgaConnectionDTO, SGAConfig sgaConfig) {
        if (sgaConnectionDTO == null) {
            return;
        }
        sgaConfig.setUrl(sgaConnectionDTO.getUrl());
        sgaConfig.setAuthUrl(sgaConnectionDTO.getAuthUrl());
        sgaConfig.setClientId(sgaConnectionDTO.getClientId());
        sgaConfig.setProfile(sgaConnectionDTO.getProfile());
        sgaConfig.setScope(sgaConnectionDTO.getScope());
        sgaConfig.setSalesChannelId(sgaConnectionDTO.getSalesChannelId());
        decorateSgaConfigWithCredentials(sgaConnectionDTO.getCredentials(), sgaConfig);
    }

    private void decorateSgaConfigWithCredentials(CredentialsBaseDTO credentials, SGAConfig sgaConfig) {
        if (credentials == null) {
            return;
        }
        sgaConfig.setUsername(credentials.getUsername());
        sgaConfig.setPassword(credentials.getPassword());
    }

    public List<String> getAvailableClubCodes() {
        return avetConfigRepository.getAvailableClubCodes();
    }

    public void linkClub(Long entityId, LinkClubConfigDTO linkClubConfigDTO) {
        validateEntityAndCheckAvet(entityId);
        avetConfigRepository.linkClubConfig(entityId, LinkClubConfigConverter.toMs(linkClubConfigDTO));
    }

    public void unlinkClub(Long entityId) {
        validateEntityAndCheckAvet(entityId);
        avetConfigRepository.unlinkClubConfig(entityId);
    }

    public void validateEntityAndCheckAvet(Long entityId) {
         validateAndGetEntity(entityId, null);
    }

    public Entity validateAndGetEntityAndCheckAvet(Long entityId) {
        return validateAndGetEntity(entityId, null);
    }

    private Entity validateAndGetEntity(Long entityId, AvetWSEnvironment avetWSEnvironment) {
        securityManager.checkEntityAccessible(entityId);
        Entity entity = entitiesRepository.getCachedEntity(entityId);
        if (BooleanUtils.isNotTrue(entity.getUseExternalAvetIntegration())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_IS_NOT_AVET);
        }
        // only the pro avetWSEnvironment can be set for the pro environment
        if (avetWSEnvironment != null &&
                EnvironmentUtils.isPro(env) && !avetWSEnvironment.equals(AvetWSEnvironment.PRO)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ENVIRONMENT);
        }
        return entity;
    }

    public InventoryProviderConfigResponseDTO getInventoryProvidersConfigByEntityId(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        ExternalConfig externalConfig = entitiesRepository.getExternalConfig(entityId);
        return fillInventoryProviders(externalConfig);
    }

    private InventoryProviderConfigResponseDTO fillInventoryProviders(ExternalConfig externalConfig) {
        InventoryProviderConfigResponseDTO inventoryProviderConfigResponseDTO = new InventoryProviderConfigResponseDTO();
        if (externalConfig != null) {
            inventoryProviderConfigResponseDTO.setInventoryProviders(externalConfig.getInventoryProviders());
        }
        return inventoryProviderConfigResponseDTO;
    }

}
