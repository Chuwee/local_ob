package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.ClubConfig;
import es.onebox.mgmt.entities.externalconfiguration.dto.ClubConfigCapacityDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ClubConfigConnectionResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ClubConfigOperativeDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ClubConfigTicketingConnectionRequestDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ClubConfigTicketingResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigRequestDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.ExternalConfigResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.members.ClubConfigMembersConnectionResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.dto.members.ClubConfigMembersResponseDTO;

public class ClubConfigConverter {

    private ClubConfigConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ExternalConfigResponseDTO toDto(ClubConfig msDto) {
        if (msDto == null) {
            return null;
        }
        ExternalConfigResponseDTO clubConfigDTO = new ExternalConfigResponseDTO();

        ClubConfigTicketingResponseDTO clubConfigTicketingResponseDTO = new ClubConfigTicketingResponseDTO();

        ClubConfigCapacityDTO clubConfigCapacityDTO = new ClubConfigCapacityDTO();
        ClubConfigOperativeDTO clubConfigOperativeDTO = new ClubConfigOperativeDTO();
        ClubConfigConnectionResponseDTO clubConfigConnectionTicketingResponseDTO = new ClubConfigConnectionResponseDTO();

        clubConfigTicketingResponseDTO.setClubConfigCapacityDTO(clubConfigCapacityDTO);
        clubConfigTicketingResponseDTO.setClubConfigOperativeDTO(clubConfigOperativeDTO);

        clubConfigConnectionTicketingResponseDTO.setIp(msDto.getIp());
        clubConfigConnectionTicketingResponseDTO.setPort(msDto.getPort());
        clubConfigConnectionTicketingResponseDTO.setName(msDto.getName());
        clubConfigConnectionTicketingResponseDTO.setProtocol(ProtocolConverter.toDto(msDto.getProtocol()));
        clubConfigConnectionTicketingResponseDTO.setUsername(msDto.getUsername());
        clubConfigConnectionTicketingResponseDTO.setPassword(msDto.getPassword());
        clubConfigConnectionTicketingResponseDTO.setPingRequestsBlocked(msDto.getPingRequestsBlocked());
        clubConfigConnectionTicketingResponseDTO.setWsConnectionVersion(WSConnectionVersionConverter.toDto(msDto.getWsConnectionVersion()));
        clubConfigTicketingResponseDTO.setClubConfigConnectionResponseDTO(clubConfigConnectionTicketingResponseDTO);

        ClubConfigMembersResponseDTO clubConfigMembersResponseDTO = new ClubConfigMembersResponseDTO();
        ClubConfigMembersConnectionResponseDTO clubConfigConnectionMembersResponseDTO = new ClubConfigMembersConnectionResponseDTO();
        clubConfigMembersResponseDTO.setMembersEnabled(msDto.getMembersEnabled());

        clubConfigConnectionMembersResponseDTO.setIp(msDto.getIpSubscriberOperations());
        clubConfigConnectionMembersResponseDTO.setPort(msDto.getPortSubscriberOperations());
        clubConfigConnectionMembersResponseDTO.setProtocol(ProtocolConverter.toDto(msDto.getProtocolSubscriberOperations()));
        clubConfigConnectionMembersResponseDTO.setUsername(msDto.getUsernameSubscriberOperations());
        clubConfigConnectionMembersResponseDTO.setPassword(msDto.getPasswordSubscriberOperations());
        clubConfigConnectionMembersResponseDTO.setWsConnectionVersion(WSConnectionVersionConverter.toDto(msDto.getWsSubscriberOperationsConnectionVersion()));
        clubConfigMembersResponseDTO.setClubConfigMembersConnectionResponseDTO(clubConfigConnectionMembersResponseDTO);

        clubConfigDTO.setClubConfigTicketingResponseDTO(clubConfigTicketingResponseDTO);
        clubConfigDTO.setClubConfigMembersResponseDTO(clubConfigMembersResponseDTO);

        clubConfigDTO.setClubCode(msDto.getClubCode());
        clubConfigDTO.setAvetWSEnvironment(AvetWSEnvironmentConverter.toDto(msDto.getAvetWSEnvironment()));

        clubConfigCapacityDTO.setCapacityNameType(CapacityNameTypeConverter.toDto(msDto.getShortName()));
        clubConfigCapacityDTO.setCapacities(msDto.getCapacities());
        clubConfigCapacityDTO.setSeason(msDto.getSeason());
        clubConfigCapacityDTO.setMembersCapacityId(msDto.getMembersCapacityId());

        clubConfigOperativeDTO.setPaymentMethod(msDto.getPaymentMethod());
        clubConfigOperativeDTO.setScheduled(msDto.getScheduled());
        clubConfigOperativeDTO.setFixedDelayMs(msDto.getFixedDelayMs());
        clubConfigOperativeDTO.setGeneratePartnerTicket(msDto.getGeneratePartnerTicket());
        clubConfigOperativeDTO.setCheckPartnerGrant(msDto.getCheckPartnerGrant());
        clubConfigOperativeDTO.setPartnerGrantCapacities(msDto.getPartnerGrantCapacities());
        clubConfigOperativeDTO.setCheckPartnerPinRegexp(msDto.getCheckPartnerPinRegexp());
        clubConfigOperativeDTO.setPartnerPinRegexp(msDto.getPartnerPinRegexp());
        clubConfigOperativeDTO.setPartnerValidationType(PartnerValidationTypeConverter.toDto(msDto.getPartnerIdToPersonId()));
        clubConfigOperativeDTO.setSendIdNumber(msDto.getSendIdNumber());
        clubConfigOperativeDTO.setIdNumberMaxLength(msDto.getIdNumberMaxLength());
        clubConfigOperativeDTO.setDigitalTicketMode(msDto.getDigitalTicketMode());

        return clubConfigDTO;
    }

    public static ClubConfig toMs(ExternalConfigRequestDTO requestDto) {
        if (requestDto == null) {
            return null;
        }

        ClubConfig clubConfig = new ClubConfig();
        clubConfig.setAvetWSEnvironment(AvetWSEnvironmentConverter.toMs(requestDto.getAvetWSEnvironment()));
        setClubConfigTicketingData(requestDto, clubConfig);
        setClubConfigMembersData(requestDto, clubConfig);
        return clubConfig;
    }

    private static void setClubConfigMembersData(ExternalConfigRequestDTO requestDto, ClubConfig clubConfig) {
        if (requestDto == null || requestDto.getClubConfigMembersRequestDTO() == null) {
            return;
        }

        clubConfig.setMembersEnabled(requestDto.getClubConfigMembersRequestDTO().getMembersEnabled());
        if (requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO() != null) {
            clubConfig.setIpSubscriberOperations(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getIp());
            clubConfig.setPortSubscriberOperations(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getPort());
            clubConfig.setProtocolSubscriberOperations(ProtocolConverter.toMs(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getProtocol()));
            clubConfig.setUsernameSubscriberOperations(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getUsername());
            clubConfig.setPasswordSubscriberOperations(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getPassword());
            clubConfig.setWsSubscriberOperationsConnectionVersion(WSConnectionVersionConverter.toMs(requestDto.getClubConfigMembersRequestDTO().getClubConfigMembersConnectionRequestDTO().getWsConnectionVersion()));
        }
    }

    private static void setClubConfigTicketingData(ExternalConfigRequestDTO requestDto, ClubConfig clubConfig) {
        if (requestDto.getClubConfigTicketingRequestDTO() == null) {
            return;
        }

        if (requestDto.getClubConfigTicketingRequestDTO().getClubConfigCapacityDTO() != null) {
            ClubConfigCapacityDTO clubConfigCapacityDTO = requestDto.getClubConfigTicketingRequestDTO().getClubConfigCapacityDTO();
            clubConfig.setShortName(CapacityNameTypeConverter.toMs(clubConfigCapacityDTO.getCapacityNameType()));
            clubConfig.setCapacities(clubConfigCapacityDTO.getCapacities());
            clubConfig.setSeason(clubConfigCapacityDTO.getSeason());
            clubConfig.setMembersCapacityId(clubConfigCapacityDTO.getMembersCapacityId());
        }

        if (requestDto.getClubConfigTicketingRequestDTO().getClubConfigTicketingConnectionRequestDTO() != null) {
            ClubConfigTicketingConnectionRequestDTO clubConfigTicketingConnectionRequestDTO = requestDto.getClubConfigTicketingRequestDTO().getClubConfigTicketingConnectionRequestDTO();
            clubConfig.setIp(clubConfigTicketingConnectionRequestDTO.getIp());
            clubConfig.setPort(clubConfigTicketingConnectionRequestDTO.getPort());
            clubConfig.setName(clubConfigTicketingConnectionRequestDTO.getName());
            clubConfig.setProtocol(ProtocolConverter.toMs(clubConfigTicketingConnectionRequestDTO.getProtocol()));
            clubConfig.setUsername(clubConfigTicketingConnectionRequestDTO.getUsername());
            clubConfig.setPassword(clubConfigTicketingConnectionRequestDTO.getPassword());
            clubConfig.setPingRequestsBlocked(clubConfigTicketingConnectionRequestDTO.getPingRequestsBlocked());
            clubConfig.setWsConnectionVersion(WSConnectionVersionConverter.toMs(clubConfigTicketingConnectionRequestDTO.getWsConnectionVersion()));

        }

        if (requestDto.getClubConfigTicketingRequestDTO().getClubConfigOperativeDTO() != null) {
            ClubConfigOperativeDTO clubConfigOperativeDTO = requestDto.getClubConfigTicketingRequestDTO().getClubConfigOperativeDTO();
            clubConfig.setPaymentMethod(clubConfigOperativeDTO.getPaymentMethod());
            clubConfig.setScheduled(clubConfigOperativeDTO.getScheduled());
            clubConfig.setFixedDelayMs(clubConfigOperativeDTO.getFixedDelayMs());
            clubConfig.setGeneratePartnerTicket(clubConfigOperativeDTO.getGeneratePartnerTicket());
            clubConfig.setCheckPartnerGrant(clubConfigOperativeDTO.getCheckPartnerGrant());
            clubConfig.setPartnerGrantCapacities(clubConfigOperativeDTO.getPartnerGrantCapacities());
            clubConfig.setCheckPartnerPinRegexp(clubConfigOperativeDTO.getCheckPartnerPinRegexp());
            clubConfig.setPartnerPinRegexp(clubConfigOperativeDTO.getPartnerPinRegexp());
            clubConfig.setPartnerIdToPersonId(PartnerValidationTypeConverter.toMs(clubConfigOperativeDTO.getPartnerValidationType()));
            clubConfig.setSendIdNumber(clubConfigOperativeDTO.getSendIdNumber());
            clubConfig.setIdNumberMaxLength(clubConfigOperativeDTO.getIdNumberMaxLength());
            clubConfig.setDigitalTicketMode(clubConfigOperativeDTO.getDigitalTicketMode());
        }
    }
}
