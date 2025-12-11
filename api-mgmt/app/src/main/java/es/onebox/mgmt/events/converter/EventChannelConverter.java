package es.onebox.mgmt.events.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestChannelCandidate;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestChannelCandidatesResponse;
import es.onebox.mgmt.datasources.ms.event.dto.event.BaseEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelSettings;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelStatusInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventInfo;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSaleRequestChannelFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannelSettings;
import es.onebox.mgmt.events.dto.channel.BaseEventChannelDTO;
import es.onebox.mgmt.events.dto.channel.ChannelEntityDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelBookingSettingsDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelInfoDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelQuotaDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelReleaseSettingsDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSaleSettingsDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSecondaryMarketSettingsDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSettingsDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelStatusInfoDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelsResponse;
import es.onebox.mgmt.events.dto.channel.EventInfoDTO;
import es.onebox.mgmt.events.dto.channel.SaleRequestChannelCandidateDTO;
import es.onebox.mgmt.events.dto.channel.SaleRequestChannelCandidatesResponseDTO;
import es.onebox.mgmt.events.dto.channel.EventSaleRequestChannelFilterDTO;
import es.onebox.mgmt.events.dto.channel.UpdateEventChannelDTO;
import es.onebox.mgmt.events.enums.EventStatus;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventChannelConverter {

    private EventChannelConverter() {
    }

    public static EventChannelsResponse fromEntity(EventChannels eventChannels) {
        if (eventChannels == null) {
            return null;
        }
        EventChannelsResponse dto = new EventChannelsResponse();
        dto.setData(fromEntity(eventChannels.getData()));
        dto.setMetadata(eventChannels.getMetadata());
        return dto;
    }

    public static List<BaseEventChannelDTO> fromEntity(List<BaseEventChannel> eventChannels) {
        if (CollectionUtils.isEmpty(eventChannels)) {
            return new ArrayList<>();
        }
        return eventChannels
                .stream()
                .map(EventChannelConverter::convertToBaseDTO)
                .collect(Collectors.toList());
    }

    public static SaleRequestChannelCandidatesResponseDTO fromMsChannelsResponse(SaleRequestChannelCandidatesResponse msResponse) {
        SaleRequestChannelCandidatesResponseDTO dto = new SaleRequestChannelCandidatesResponseDTO();
        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData()
                .stream()
                .map(EventChannelConverter::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public static EventSaleRequestChannelFilter toMs(EventSaleRequestChannelFilterDTO in, Long entityId,
                                                     List<Long> visibleEntities, Long operatorId) {
        EventSaleRequestChannelFilter out = new EventSaleRequestChannelFilter();
        out.setEntityId(entityId);
        out.setName(in.getName());
        out.setIncludeThirdPartyChannels(in.getIncludeThirdPartyChannels());
        out.setVisibleEntities(visibleEntities);
        out.setOperatorId(operatorId);
        out.setOffset(in.getOffset());
        out.setLimit(in.getLimit());
        out.setDestinationChannelType(in.getDestinationChannelType());

        if (in.getType() != null) {
            out.setType(in.getType().stream()
                    .map(type -> es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype.getById(type.getId()))
                    .toList());
        }
        return out;
    }

    private static SaleRequestChannelCandidateDTO toDTO(SaleRequestChannelCandidate in) {
        SaleRequestChannelCandidateDTO out = new SaleRequestChannelCandidateDTO();

        out.setId(in.getId());
        out.setName(in.getName());

        ChannelEntityDTO entity = new ChannelEntityDTO();
        entity.setId(in.getEntityId());
        entity.setName(in.getEntityName());
        entity.setLogo(in.getEntityLogo());
        out.setEntity(entity);

        out.setStatus(ChannelStatus.getById(in.getStatus().getId()));
        if (in.getSubtype() != null) {
            out.setType(ChannelSubtype.getById(in.getSubtype().getIdSubtipo()));
        }
        return out;
    }

    private static BaseEventChannelDTO convertToBaseDTO(BaseEventChannel eventChannel) {
        if (eventChannel == null) {
            return null;
        }
        return fillBaseEventChannel(new BaseEventChannelDTO(), eventChannel, null, null);
    }

    private static BaseEventChannelDTO fillBaseEventChannel(BaseEventChannelDTO target, BaseEventChannel eventChannel,
                                                            ChannelLanguagesDTO languages, Boolean forceSquarePictures) {
        if (target == null) {
            return null;
        }
        target.setChannel(getEventChannelInfo(eventChannel.getChannel(), forceSquarePictures));
        target.setEvent(getEventInfo(eventChannel.getEvent()));
        target.setStatus(getStatusInfo(eventChannel.getStatus()));
        target.setSettings(getSettings(eventChannel.getSettings(), languages));
        target.setProviderPlanSettings(toDTO(eventChannel.getProviderPlanSettings()));
        return target;
    }

    private static EventChannelInfoDTO getEventChannelInfo(EventChannelInfo eventChannelInfo, Boolean forceSquarePictures) {
        if (eventChannelInfo == null) {
            return null;
        }
        EventChannelInfoDTO dto = new EventChannelInfoDTO(eventChannelInfo.getId(), eventChannelInfo.getName());
        dto.setType(ChannelSubtype.getById(eventChannelInfo.getType().getIdSubtipo()));
        dto.setIsV4(CommonUtils.isTrue(eventChannelInfo.getV4Enabled())
               || CommonUtils.isTrue(eventChannelInfo.getV4ConfigEnabled()));
        dto.setEntity(new ChannelEntityDTO());
        dto.getEntity().setId(eventChannelInfo.getEntityId());
        dto.getEntity().setName(eventChannelInfo.getEntityName());
        dto.getEntity().setLogo(eventChannelInfo.getEntityLogo());
        dto.setFavorite(eventChannelInfo.getFavorite());
        if (eventChannelInfo.getWhitelabelType() != null) {
            dto.setWhitelabelType(WhitelabelType.valueOf(eventChannelInfo.getWhitelabelType().name()));
        }
        dto.setForceSquarePictures(forceSquarePictures);
        return dto;
    }

    private static EventInfoDTO getEventInfo(EventInfo eventInfo) {
        if (eventInfo == null) {
            return null;
        }
        EventInfoDTO dto = new EventInfoDTO();
        dto.setId(eventInfo.getId());
        dto.setStatus(EventStatus.valueOf(eventInfo.getStatus().name()));
        return dto;
    }

    private static EventChannelSettingsDTO getSettings(EventChannelSettings eventChannelSettings, ChannelLanguagesDTO languages) {
        if (eventChannelSettings == null) {
            return null;
        }
        EventChannelSettingsDTO dto = new EventChannelSettingsDTO();
        dto.setUseEventDates(eventChannelSettings.getUseEventDates());

        dto.setRelease(new EventChannelReleaseSettingsDTO());
        dto.getRelease().setDate(eventChannelSettings.getReleaseDate());
        dto.getRelease().setEnabled(eventChannelSettings.getReleaseEnabled());

        dto.setSale(new EventChannelSaleSettingsDTO());
        dto.getSale().setEnabled(eventChannelSettings.getSaleEnabled());
        dto.getSale().setEndDate(eventChannelSettings.getSaleEndDate());
        dto.getSale().setStartDate(eventChannelSettings.getSaleStartDate());

        dto.setBooking(new EventChannelBookingSettingsDTO());
        dto.getBooking().setEnabled(eventChannelSettings.getBookingEnabled());
        dto.getBooking().setEndDate(eventChannelSettings.getBookingEndDate());
        dto.getBooking().setStartDate(eventChannelSettings.getBookingStartDate());

        dto.setSecondaryMarketSale(new EventChannelSecondaryMarketSettingsDTO());
        dto.getSecondaryMarketSale().setEnabled(eventChannelSettings.getSecondaryMarketEnabled());
        dto.getSecondaryMarketSale().setStartDate(eventChannelSettings.getSecondaryMarketStartDate());
        dto.getSecondaryMarketSale().setEndDate(eventChannelSettings.getSecondaryMarketEndDate());

        dto.setLanguages(languages);

        return dto;
    }

    private static EventChannelStatusInfoDTO getStatusInfo(EventChannelStatusInfo eventChannelStatusInfo) {
        if (eventChannelStatusInfo == null) {
            return null;
        }
        EventChannelStatusInfoDTO dto = new EventChannelStatusInfoDTO();
        dto.setRelease(eventChannelStatusInfo.getRelease());
        dto.setRequest(eventChannelStatusInfo.getRequest());
        dto.setSale(eventChannelStatusInfo.getSale());
        return dto;
    }

    public static EventChannelDTO fromEntity(EventChannel eventChannel, ChannelLanguagesDTO languages, Boolean forceSquarePictures) {
        EventChannelDTO eventChannelDTO = new EventChannelDTO();
        fillBaseEventChannel(eventChannelDTO, eventChannel, languages, forceSquarePictures);
        if (eventChannel.getSaleGroups() != null) {
            eventChannelDTO.setQuotas(eventChannel.getSaleGroups().stream().map(saleGroup -> {
                EventChannelQuotaDTO quota = new EventChannelQuotaDTO();
                quota.setId(saleGroup.getId());
                quota.setDescription(saleGroup.getDescription());
                quota.setTemplateId(saleGroup.getTemplateId());
                quota.setTemplateName(saleGroup.getTemplateName());
                quota.setSelected(saleGroup.getSelected());
                return quota;
            }).collect(Collectors.toList()));
        }
        eventChannelDTO.setUseAllQuotas(eventChannel.getUseAllSaleGroups());
        return eventChannelDTO;
    }

    public static UpdateEventChannel fromDTO(UpdateEventChannelDTO dto) {
        UpdateEventChannel updateEventChannel = new UpdateEventChannel();
        updateEventChannel.setSaleGroups(dto.getQuotas());
        updateEventChannel.setUseAllSaleGroups(dto.getUseAllQuotas());
        if (dto.getSettings() != null) {
            updateEventChannel.setSettings(fromDTO(dto.getSettings()));
        }
        updateEventChannel.setProviderPlanSettings(fromDTO(dto.getProviderPlanSettings()));
        return updateEventChannel;
    }

    private static UpdateEventChannelSettings fromDTO(EventChannelSettingsDTO in) {
        UpdateEventChannelSettings out = new UpdateEventChannelSettings();
        out.setUseEventDates(in.getUseEventDates());

        if (in.getBooking() != null) {
            out.setBookingEnabled(in.getBooking().getEnabled());
            out.setBookingEndDate(in.getBooking().getEndDate());
            out.setBookingStartDate(in.getBooking().getStartDate());
        }

        if (in.getRelease() != null) {
            out.setReleaseEnabled(in.getRelease().getEnabled());
            out.setReleaseDate(in.getRelease().getDate());
        }

        if (in.getSale() != null) {
            out.setSaleEnabled(in.getSale().getEnabled());
            out.setSaleEndDate(in.getSale().getEndDate());
            out.setSaleStartDate(in.getSale().getStartDate());
        }

        if (in.getSecondaryMarketSale() != null) {
            out.setSecondaryMarketEnabled(in.getSecondaryMarketSale().getEnabled());
            out.setSecondaryMarketStartDate(in.getSecondaryMarketSale().getStartDate());
            out.setSecondaryMarketEndDate(in.getSecondaryMarketSale().getEndDate());
        }

        return out;
    }

    private static es.onebox.mgmt.events.dto.channel.ProviderPlanSettingsDTO toDTO(
            es.onebox.mgmt.datasources.ms.event.dto.event.ProviderPlanSettings entity) {
        if (entity == null) {
            return null;
        }
        es.onebox.mgmt.events.dto.channel.ProviderPlanSettingsDTO dto = new es.onebox.mgmt.events.dto.channel.ProviderPlanSettingsDTO();
        dto.setSyncSessionsAsHidden(entity.getSyncSessionsAsHidden());
        dto.setSyncSurcharges(entity.getSyncSurcharges());
        dto.setSyncSessionLabels(entity.getSyncSessionLabels());
        dto.setSyncSessionPics(entity.getSyncSessionPics());
        dto.setSyncSessionTypeOrdering(entity.getSyncSessionTypeOrdering());
        dto.setSyncSessionTypeDetails(entity.getSyncSessionTypeDetails());
        dto.setSyncMainPlanTitle(entity.getSyncMainPlanTitle());
        dto.setSyncMainPlanDescription(entity.getSyncMainPlanDescription());
        dto.setSyncMainPlanImages(entity.getSyncMainPlanImages());
        return dto;
    }

    private static es.onebox.mgmt.datasources.ms.event.dto.event.ProviderPlanSettings fromDTO(
            es.onebox.mgmt.events.dto.channel.ProviderPlanSettingsDTO dto) {
        if (dto == null) {
            return null;
        }
        es.onebox.mgmt.datasources.ms.event.dto.event.ProviderPlanSettings entity = new es.onebox.mgmt.datasources.ms.event.dto.event.ProviderPlanSettings();
        entity.setSyncSessionsAsHidden(dto.getSyncSessionsAsHidden());
        entity.setSyncSurcharges(dto.getSyncSurcharges());
        entity.setSyncSessionLabels(dto.getSyncSessionLabels());
        entity.setSyncSessionPics(dto.getSyncSessionPics());
        entity.setSyncSessionTypeOrdering(dto.getSyncSessionTypeOrdering());
        entity.setSyncSessionTypeDetails(dto.getSyncSessionTypeDetails());
        entity.setSyncMainPlanTitle(dto.getSyncMainPlanTitle());
        entity.setSyncMainPlanDescription(dto.getSyncMainPlanDescription());
        entity.setSyncMainPlanImages(dto.getSyncMainPlanImages());
        return entity;
    }
}
